package ru.practicum.ewm.controller.pub.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.convert.TimeConverter;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.stat.ClientStatService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.PublicGetParams;
import ru.practicum.ewm.event.model.EventSortMode;
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
                                         @RequestParam(required = false) EventSortMode sort,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {

        //валидация
        if (!TimeConverter.validateRange(rangeStart, rangeEnd)) {
            throw new BadRequestException("Недопустимые границы временного диапазона");
        }
        log.debug("Запрошено получение списка событий");
        statService.setEvent(request.getRequestURI(), request.getRemoteAddr());
        log.debug("Запрос на получение списка событий добавлен в статистику");
        PublicGetParams params = new PublicGetParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        return eventService.searchEvents(params, from, size);
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
