package ru.practicum.ewm.controller.admin_api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
@Validated
public class AdminEventController {
    private EventService eventService;

    //запрос событий по параметрам
    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) Long[] users,
                                        @RequestParam(required = false) String[] states,
                                        @RequestParam(required = false) Long[] categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("Запрошено получение списка событий администратором");
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
    }

    //обновление администратором события и его статуса (отклонение/регистрация)
    @PatchMapping(path = "/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest eventDto) {
        log.info("Запрошено обновление события с идентификатором " + eventId);
        return eventService.updateEventByAdmin(eventId, eventDto);
    }
}
