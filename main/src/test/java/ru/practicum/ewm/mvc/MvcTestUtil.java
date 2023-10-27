package ru.practicum.ewm.mvc;

import ru.practicum.ewm.common.convert.TimeConverter;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.ArrayList;
import java.util.List;

public class MvcTestUtil {
    public static final String pattern = "0123456789";

    public static NewEventDto createNewEventDto() {
        NewEventDto eventDto = new NewEventDto();
        eventDto.setTitle("abc");
        eventDto.setAnnotation(pattern.repeat(2));
        eventDto.setDescription(pattern.repeat(2));
        eventDto.setCategory(1L);
        eventDto.setEventDate(TimeConverter.formatNow());
        eventDto.setLocation(new Location(0, 0));
        return eventDto;
    }

    public static EventShortDto createEventShortDto() {
        EventShortDto eventDto = new EventShortDto();
        eventDto.setId(17L);
        eventDto.setTitle("abc");
        eventDto.setAnnotation(pattern.repeat(2));
        eventDto.setEventDate(TimeConverter.formatNow());
        return eventDto;
    }

    public static UpdateEventAdminRequest createEventAdminRequest() {
        UpdateEventAdminRequest eventDto = new UpdateEventAdminRequest();
        eventDto.setTitle("abc");
        eventDto.setAnnotation(pattern.repeat(2));
        eventDto.setDescription(pattern.repeat(2));
        eventDto.setCategory(1L);
        eventDto.setEventDate(TimeConverter.formatNow());
        eventDto.setLocation(new Location(0, 0));
        return eventDto;
    }

    public static UpdateEventUserRequest createEventUserRequest() {
        UpdateEventUserRequest eventDto = new UpdateEventUserRequest();
        eventDto.setTitle("abc");
        eventDto.setAnnotation(pattern.repeat(2));
        eventDto.setDescription(pattern.repeat(2));
        eventDto.setCategory(1L);
        eventDto.setEventDate(TimeConverter.formatNow());
        eventDto.setLocation(new Location(0, 0));
        return eventDto;
    }

    public static EventRequestStatusUpdateRequest createStatusUpdateRequest() {
        return new EventRequestStatusUpdateRequest(new ArrayList<>(List.of(1L, 2L, 3L)), RequestStatus.CONFIRMED);
    }

    public static EventRequestStatusUpdateResult createStatusUpdateResul() {
        ParticipationRequestDto requestsToConfirm = createRequestDto();
        ParticipationRequestDto requestsToReject = createRequestDto();
        requestsToReject.setId(2L);
        return new EventRequestStatusUpdateResult(List.of(requestsToConfirm), List.of(requestsToReject));
    }

    public static EventFullDto copyEventFullDto(NewEventDto inDto) {
        EventFullDto outDto = new EventFullDto();
        outDto.setId(17L);
        outDto.setTitle(inDto.getTitle());
        outDto.setAnnotation(inDto.getAnnotation());
        outDto.setDescription(inDto.getDescription());
        outDto.setEventDate(inDto.getEventDate());
        outDto.setLocation(inDto.getLocation());
        return outDto;
    }

    public static EventFullDto copyEventFullDto(UpdateEventAdminRequest inDto) {
        EventFullDto outDto = new EventFullDto();
        outDto.setId(17L);
        outDto.setTitle(inDto.getTitle());
        outDto.setAnnotation(inDto.getAnnotation());
        outDto.setDescription(inDto.getDescription());
        outDto.setEventDate(inDto.getEventDate());
        outDto.setLocation(inDto.getLocation());
        return outDto;
    }

    public static EventFullDto copyEventFullDto(UpdateEventUserRequest inDto) {
        EventFullDto outDto = new EventFullDto();
        outDto.setId(17L);
        outDto.setTitle(inDto.getTitle());
        outDto.setAnnotation(inDto.getAnnotation());
        outDto.setDescription(inDto.getDescription());
        outDto.setEventDate(inDto.getEventDate());
        outDto.setLocation(inDto.getLocation());
        return outDto;
    }

    public static ParticipationRequestDto createRequestDto() {
        return new ParticipationRequestDto(
                TimeConverter.formatNow(), 17L, 1L,13L, RequestStatus.PENDING.name());
    }

    public static NewCompilationDto createNewCompilationDto() {
        return new NewCompilationDto(new ArrayList<>(), false, "x");
    }

    public static UpdateCompilationRequest createUpdateCompilationDto() {
        return new UpdateCompilationRequest(new ArrayList<>(List.of(1L, 2L, 3L)), false, "x");
    }

    public static CompilationDto createCompilationDto() {
        return new CompilationDto(new ArrayList<>(), 17L, false, "x");
    }
}
