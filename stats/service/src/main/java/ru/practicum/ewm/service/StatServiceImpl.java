package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatInDto;
import ru.practicum.ewm.StatOutDto;
import ru.practicum.ewm.model.StatDtoMapper;
import ru.practicum.ewm.storage.StatRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    public List<StatOutDto> getStat(String start, String end, String[] uris, boolean unique) {
        if ((uris == null) || (uris.length == 0)) { //целевые эндпойнты не заданы
            if (unique) { //требуются данные без повторов
                return statRepository.getAllUniqueHits(start, end); //выдаем сжатую статистику
            } else { //повторы разрешены
                return statRepository.getAllHits(start, end); //выдаем всю статистику за нужный период
            }
        }
        //если эндпойнты указаны - выдаем статистику только для них
        if (unique) { //требуются данные без повторов
            return statRepository.getUniqueHitsForUris(start, end, uris);
        } else { //повторы разрешены
            return statRepository.getHitsForUris(start, end, uris);
        }
    }

    @Override
    public void putHit(StatInDto stat) {
        statRepository.save(StatDtoMapper.toHit(stat));
    }
}
