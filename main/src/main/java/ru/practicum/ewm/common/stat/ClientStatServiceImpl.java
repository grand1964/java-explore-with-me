package ru.practicum.ewm.common.stat;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.StatInDto;
import ru.practicum.ewm.StatOutDto;
import ru.practicum.ewm.common.convert.TimeConverter;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ClientStatServiceImpl implements ClientStatService {
    private static final String STAT_APP = "ewm-main-service";
    private StatClient statClient;

    //сохранение статистики
    @Override
    public void setEvent(String uri, String ip) {
        ResponseEntity<Void> response = statClient.post(
                new StatInDto(STAT_APP, uri, ip, TimeConverter.formatNow()));
        if (response.getStatusCode().value() != 201) {
            throw new RuntimeException("Ошибка при сохранении статистики, uri: " + uri);
        }
    }

    //заполняет поле статистики в списке событий
    @Override
    public List<EventShortDto> getStat(List<EventShortDto> events) {
        ResponseEntity<List<StatOutDto>> response = statClient.get(
                TimeConverter.formatCurrentDay(), TimeConverter.formatNow(), getUris(events), true);
        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Ошибка при запросе статистики");
        }
        List<StatOutDto> stats = response.getBody();
        //преобразуем статистику в отображение
        Map<Long, Long> map = parseMultipleStat(stats);
        //заполняем количества просмотров в событиях
        for (EventShortDto event : events) {
            event.setViews(map.get(event.getId()));
        }
        return events;
    }

    //заполняет поле статистики в одном событии
    @Override
    public EventFullDto getStat(EventFullDto dto) {
        ResponseEntity<List<StatOutDto>> response = statClient.get(
                TimeConverter.formatCurrentDay(), TimeConverter.formatNow(), getUri(dto), true);
        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Ошибка при запросе статистики");
        }
        List<StatOutDto> stats = response.getBody();
        if ((stats == null) || (stats.isEmpty())) {
            throw new RuntimeException("Ошибка при запросе статистики");
        }
        dto.setViews(parseSingleStat(response.getBody().get(0)));
        return dto;
    }

    ///////////////////////// Вспомогательные методы /////////////////////////

    //формирует массив uri по списку событий
    private String[] getUris(List<EventShortDto> dtos) {
        int size = dtos.size();
        String[] uris = new String[size];
        for (int i = 0; i < size; i++) {
            uris[i] = "/events/" + dtos.get(i).getId();
        }
        return uris;
    }

    //формирует одноэлементный массив uri по событию
    private String[] getUri(EventFullDto dto) {
        return new String[]{"/events/" + dto.getId()};
    }

    //выделяет параметр из uri
    private Long parseUri(String uri) {
        int slashIndex = uri.lastIndexOf("/");
        if (slashIndex == 0) { //events без параметров
            return null;
        } else {
            return Long.parseLong(uri.substring(slashIndex + 1));
        }
    }

    //преобразует список объектов статистики в пары (id, число записей)
    private Map<Long, Long> parseMultipleStat(List<StatOutDto> dtoList) {
        Map<Long, Long> map = new HashMap<>();
        if (dtoList != null) {
            for (StatOutDto dto : dtoList) {
                Long uri = parseUri(dto.getUri());
                if (uri != null) {
                    map.put(uri, dto.getHits());
                }
            }
        }
        return map;
    }

    //выделяет число вызовов из объекта статистики
    private Long parseSingleStat(StatOutDto dto) {
        long result = 0L;
        if (dto != null) {
            Long uri = parseUri(dto.getUri());
            if (uri != null) {
                result = dto.getHits();
            }
        }
        return result;
    }
}
