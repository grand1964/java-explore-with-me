package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class RequestDtoMapper {
    public static Request toRequest(ParticipationRequestDto dto, Event event, User requester) {
        Request request = new Request();
        request.setId(dto.getId());
        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(dto.getCreated());
        request.setStatus(Enum.valueOf(RequestStatus.class, dto.getStatus()));
        return request;
    }

    public static ParticipationRequestDto toRequestDto(Request request) {
        return new ParticipationRequestDto(request.getCreated(), request.getEvent().getId(),
                request.getId(), request.getRequester().getId(), request.getStatus().name());
    }

    public static List<ParticipationRequestDto> toRequestDtoList(List<Request> request) {
        return request.stream().map(RequestDtoMapper::toRequestDto).collect(Collectors.toList());
    }
}
