package ru.practicum.ewm.controller.public_api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.stat.ClientStatService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
@Validated
public class PublicEventController {
    private EventService eventService;
    private ClientStatService statService;

    //поиск событий по параметрам
    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) Long[] categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {
        log.debug("Запрошено получение списка событий");
        statService.setEvent(request.getRequestURI(), request.getRemoteAddr());
        log.debug("Запрос на получение списка событий добавлен в статистику");
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return eventService.searchEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    //полная информация о событии
    @GetMapping(path = "/{eventId}")
    public EventFullDto getEventById(@PathVariable long eventId, HttpServletRequest request) {
        log.debug("Запрошена информация о событии с идентификатором " + eventId);
        statService.setEvent(request.getRequestURI(), request.getRemoteAddr());
        log.debug("Запрос на получение информации о событии добавлен в статистику");
        return eventService.getEventById(eventId);
    }
}
