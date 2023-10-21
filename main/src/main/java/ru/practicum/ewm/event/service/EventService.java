package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getEventsByAdmin(Long[] users, String[] states, Long[] categories,
                                        String rangeStart, String rangeEnd, Pageable pageable);

    List<EventShortDto> searchEvents(
            String text, Long[] categories, Boolean paid, String rangeStart, String rangeEnd,
            Boolean onlyAvailable, String sort, int from, int size);

    EventFullDto getEventById(long eventId);

    List<EventShortDto> getEventsByUser(long userId, Pageable pageable);

    EventFullDto getEventWithIdByUser(long userId, long eventId);

    List<ParticipationRequestDto> getRequestsForEvent(long userId, long eventId);

    EventFullDto createEvent(long userId, NewEventDto eventDto);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest eventDto);

    EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest eventDto);

    EventRequestStatusUpdateResult updateRequestsForEvent(long userId, long eventId,
                                                          EventRequestStatusUpdateRequest requestDto);
}
