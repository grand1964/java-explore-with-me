package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.convert.TimeConverter;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestDtoMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        return RequestDtoMapper.toRequestDtoList(requestRepository.getUserRequestsForForeignEvents(userId));
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие " + eventId + " не найдено")
        );
        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь " + userId + " не найден")
        );
        //валидация события
        if (event.getInitiator().getId() == userId) { //в своем событии нельзя участвовать
            throw new ConflictException("Заявка пользователя " + userId + " на участие в своем событии");
        }
        if (event.getState() != EventState.PUBLISHED) { //событие не опубликовано
            throw new ConflictException("Заявка на участие в неопубликованном событии " + eventId);
        }
        //TODO Проверить!!!
        if ((event.getParticipantLimit() > 0)
                && (event.getConfirmedRequests().intValue() == event.getParticipantLimit())) {
            throw new ConflictException("Свободных мест в событии " + eventId + " уже нет");
        }
        Request request = new Request(
                null, event, requester, TimeConverter.formatNow(), RequestStatus.PENDING);
        if (!event.getRequestModeration() || (event.getParticipantLimit() == 0)) { //число участников не ограничено
            request.setStatus(RequestStatus.CONFIRMED); //выставляем подтверждение
            event.setConfirmedRequests(event.getConfirmedRequests() + 1); //меняем число одобренных заявок
            eventRepository.save(event); //сохраняем изменения в базе
        }
        Request newRequest = requestRepository.save(request);
        return RequestDtoMapper.toRequestDto(newRequest);
    }

    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Заявка " + requestId + " не найдена")
        );
        //меняем при необходимости число одобренных заявок
        Event event = request.getEvent();
        if (event.getRequestModeration() && event.getParticipantLimit() > 0 &&
                request.getStatus() == RequestStatus.CONFIRMED) { //отменяемая заявка занимает место
            event.setConfirmedRequests(event.getConfirmedRequests() - 1); //освобождаем место
            eventRepository.save(event); //меняем событие в базе
        }
        //меняем статус заявки и сохраняем ее в базе
        request.setStatus(RequestStatus.CANCELED); //статус отмены
        Request newRequest = requestRepository.save(request);
        return RequestDtoMapper.toRequestDto(newRequest);
    }
}
