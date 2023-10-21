package ru.practicum.ewm.controller.private_api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@AllArgsConstructor
@Validated
public class PrivateEventController {
    private EventService eventService;

    ///////////////////////////// Запросы событий ////////////////////////////

    //запрос своих событий пользователем
    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("Запрошено получение списка своих событий инициатором");
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return eventService.getEventsByUser(userId, pageable);
    }

    //запрос пользователем информации о своем событии
    @GetMapping(path = "/{eventId}")
    public EventFullDto getEventWithIdByUser(@PathVariable long userId, @PathVariable long eventId) {
        log.debug("Инициатором запрошена информация своем событии");
        return eventService.getEventWithIdByUser(userId, eventId);
    }

    //запрос пользователем информации о заявках на участие в своем событии
    @GetMapping(path = "/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForEvent(@PathVariable long userId,
                                                             @PathVariable long eventId) {
        log.debug("Инициатором запрошена информация о заявках на участие в своем событии");
        return eventService.getRequestsForEvent(userId, eventId);
    }

    ///////////////////////////// Создание событий ///////////////////////////

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@PathVariable long userId,
                                                    @Valid @RequestBody NewEventDto eventDto) {
        log.info("Запрошено создание нового события");
        return ResponseEntity
                .status(201)
                .body(eventService.createEvent(userId, eventDto));
    }

    //////////////////////////// Обновление событий //////////////////////////

    @PatchMapping(path = "/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable long userId,
                                          @PathVariable long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest eventDto) {
        log.info("Запрошено обновление пользователем своего события");
        return eventService.updateEventByUser(userId, eventId, eventDto);
    }

    @PatchMapping(path = "/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestForEvent(@PathVariable long userId,
                                                                @PathVariable long eventId,
                                                                @RequestBody EventRequestStatusUpdateRequest requestDto) {
        log.info("Запрошено согласование заявок на события");
        return eventService.updateRequestsForEvent(userId, eventId, requestDto);
    }
}
