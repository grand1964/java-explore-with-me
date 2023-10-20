package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.common.convert.TimeConverter;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.common.stat.ClientStatService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.AdminStateAction;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestDtoMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private RequestRepository requestRepository;
    private ClientStatService statService;

    //////////////////////////// Групповые запросы ///////////////////////////

    //запрос событий администратором
    @Override
    public List<EventFullDto> getEventsByAdmin(Long[] users, String[] states, Long[] categories,
                                        String rangeStart, String rangeEnd, Pageable pageable) {
        //валидация времени
        if (!TimeConverter.validateRange(rangeStart, rangeEnd)) {
            throw new BadRequestException("Недопустимые границы временного диапазона");
        }
        //построение критериев запроса
        BooleanExpression searchCriteria = Expressions.asBoolean(true).isTrue();
        if (users != null) {
            searchCriteria = searchCriteria
                    .and(QEvent.event.initiator.id.in(users));
        }
        if (states != null) {
            List<EventState> stateList = Arrays.stream(states)
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
            searchCriteria = searchCriteria
                    .and(QEvent.event.state.in(stateList));
        }
        if (categories != null) {
            searchCriteria = searchCriteria
                    .and(QEvent.event.category.id.in(categories));
        }
        if ((rangeStart != null) && (rangeEnd != null)) { //диапазон задан корректно
            searchCriteria = searchCriteria.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));
        } else { //ищем будущие события
            searchCriteria = searchCriteria.and(QEvent.event.eventDate.gt(TimeConverter.formatNow()));
        }
        return eventRepository.findAll(searchCriteria, pageable)
                    .map(EventDtoMapper::toEventFullDto)
                    .getContent();
    }

    //публичный поиск событий по параметрам
    @Override
    public List<EventShortDto> searchEvents(
            String text, Long[] categories, Boolean paid, String rangeStart, String rangeEnd,
            Boolean onlyAvailable, String sort, int from, int size) {
        //валидация
        if ((sort != null) && (!sort.equals("EVENT_DATE")) && (!sort.equals("VIEWS"))) {
            throw new BadRequestException("Недопустимый параметр сортировки: " + sort);
        }
        if (!TimeConverter.validateRange(rangeStart, rangeEnd)) {
            throw new BadRequestException("Недопустимые границы временного диапазона");
        }
        //начало построения критериев запроса: все события должны быть одобрены
        BooleanExpression searchCriteria = QEvent.event.state.eq(EventState.PUBLISHED);
        if (text != null) {
            searchCriteria = searchCriteria
                    .and(QEvent.event.annotation.containsIgnoreCase(text)
                            .or(QEvent.event.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            searchCriteria = searchCriteria.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            searchCriteria = searchCriteria.and(QEvent.event.paid.eq(paid));
        }
        if ((rangeStart != null) && (rangeEnd != null)) { //диапазон задан корректно
            searchCriteria = searchCriteria.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));
        } else { //ищем будущие события
            searchCriteria = searchCriteria.and(QEvent.event.eventDate.gt(TimeConverter.formatNow()));
        }
        if ((onlyAvailable != null) && onlyAvailable) {
            searchCriteria = searchCriteria
                    .and(QEvent.event.participantLimit.gt(QEvent.event.confirmedRequests)
                            .or(QEvent.event.participantLimit.eq(0)));
        }

        //выполняем запрос
        PageRequest pageable;
        if (sort == null) {
            pageable = PageRequest.of(from / size, size);
        } else {
            switch (sort) {
                case "EVENT_DATE":
                    pageable = PageRequest.of(from / size, size, Sort.by("eventDate").descending());
                    break;
                case "VIEWS":
                    pageable = null;
                    break;
                default:
                    throw new BadRequestException("Некорректный параметр сортировки: " + sort);
            }
        }
        if (pageable != null) { //сортировка не требуется или она по числу просмотров
            //выполняем запрос с пагинацией
            List<EventShortDto> dtoList = eventRepository.findAll(searchCriteria, pageable)
                    .map(EventDtoMapper::toEventShortDto)
                    .getContent();
            //добавляем в результат просмотры из сервиса статистики и возвращаем его
            return statService.getStat(dtoList);
        }

        //далее - случай сортировки по просмотрам
        //читаем данные из базы без пагинации
        Iterable<Event> events = eventRepository.findAll(searchCriteria);
        //преобразуем их в список dto
        List<EventShortDto> dtoList = EventDtoMapper.toEventShortDtoList(events);
        //добавляем в результат просмотры из сервиса статистики
        dtoList = statService.getStat(dtoList);
        //если данных мало - пагинация дает пустой массив
        int dataSize = dtoList.size();
        if (from >= dataSize) {
            return new ArrayList<>();
        }
        //вычисляем параметры пагинации
        int toSkip = from / size * size; //начало блока данных
        int limit = Math.min(size, dataSize - toSkip); //размер блока данных
        //выполняем пагинацию с сортировкой вручную и возвращаем результат
        return dtoList.stream()
                .sorted(Comparator.comparingLong(EventShortDto::getViews).reversed())
                .skip(toSkip)
                .limit(limit)
                .collect(Collectors.toList());
    }

    //запрос инициатором своих событий
    public List<EventShortDto> getEventsByUser(long userId, Pageable pageable) {
        return eventRepository.getEventsByInitiator(userId, pageable)
                .map(EventDtoMapper::toEventShortDto)
                .getContent();
    }

    /////////////////////// Запросы информации о событии /////////////////////

    //публичный запрос события
    public EventFullDto getEventById(long eventId) {
        Event event = eventRepository.findByIdAndStateIs(eventId, EventState.PUBLISHED).orElseThrow(
                () -> new NotFoundException("Событие с идентификатором " + eventId + " не найдено")
        );
        //проверяем, опубликовано ли событие
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие с идентификатором " + eventId + " не опубликовано");
        }
        EventFullDto dto = EventDtoMapper.toEventFullDto(event);
        return statService.getStat(dto);
    }

    //запрос инициатором информации о своем событии
    public EventFullDto getEventWithIdByUser(long userId, long eventId) {
        Event event = eventRepository.getEventByInitiator(userId, eventId).orElseThrow(
                () -> new NotFoundException("Событие с идентификатором " + eventId + " не найдено")
        );
        return EventDtoMapper.toEventFullDto(event);
    }

    //запрос пользователем информации о запросах на участие в своем событии
    public List<ParticipationRequestDto> getRequestsForEvent(long userId, long eventId) {
        List<Request> requests = requestRepository.getRequestsForEvent(userId, eventId);
        return requests.stream().map(RequestDtoMapper::toRequestDto).collect(Collectors.toList());
    }

    ///////////////////////////// Создание событий ///////////////////////////

    @Transactional
    @Override
    public EventFullDto createEvent(long userId, NewEventDto eventDto) {
        //проверка временных условий
        String now = TimeConverter.formatNow();
        if ((eventDto.getEventDate() != null) && TimeConverter.afterOrEquals(now, eventDto.getEventDate())) {
            throw new BadRequestException("Попытка установить прошедшую дату события");
        }
        if (TimeConverter.getDistanceInMinutes(eventDto.getEventDate(), now) < 120) {
            throw new ConflictException("До события осталось менее двух часов");
        }
        //читаем инициатора
        User initiator = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Не найден инициатор с идентификатором " + userId)
        );
        //читаем категорию
        long categoryId = eventDto.getCategory();
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new BadRequestException("Не найдена категория с идентификатором " + categoryId)
        );
        //генерируем новое событие по параметрам
        Event event = EventDtoMapper.toEvent(eventDto, category, initiator);
        //задаем дату создания
        event.setCreatedOn(TimeConverter.formatNow());
        //пытаемся сохранить его в базе
        Event newEvent = eventRepository.save(event);
        //если успешно - возвращаем сохраненное событие
        return EventDtoMapper.toEventFullDto(newEvent);
    }

    //////////////////////////// Обновление событий //////////////////////////

    //обновление события администратором с подтверждением/отклонением
    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest eventDto) {
        //читаем оригинальное событие или выдаем ошибку
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с идентификатором " + eventId + " не найдено")
        );
        //проверка временных условий
        String now = TimeConverter.formatNow();
        if (TimeConverter.getDistanceInMinutes(event.getEventDate(), now) <= 60) {
            throw new ConflictException("До начала события осталось меньше часа");
        }
        if ((eventDto.getEventDate() != null) && TimeConverter.afterOrEquals(now, eventDto.getEventDate())) {
            throw new BadRequestException("Попытка установить прошедшую дату события");
        }
        //проверка состояний
        if ((event.getState() != EventState.PENDING) &&
                (eventDto.getStateAction() == AdminStateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Нельзя публиковать событие, не ожидающее публикации");
        }
        if ((event.getState() == EventState.PUBLISHED) &&
                (eventDto.getStateAction() == AdminStateAction.REJECT_EVENT)) {
            throw new ConflictException("Нельзя отклонить опубликованное событие");
        }
        //если все корректно - обновляем событие
        EventDtoMapper.patchAdminDtoToEvent(eventDto, event);
        //читаем категорию
        Long catId = eventDto.getCategory();
        if (catId != null) {
            Category category = categoryRepository.findById(catId).orElseThrow(
                    () -> new BadRequestException("Категория " + catId + " не найдена")
            );
            event.setCategory(category);
        }
        //обновляем состояние
        if (eventDto.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
            event.setState(EventState.PUBLISHED);
        } else {
            event.setState(EventState.CANCELED);
        }
        event.setPublishedOn(now);
        //сохраняем измененное событие
        Event newEvent = eventRepository.save(event);
        return EventDtoMapper.toEventFullDto(newEvent);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest eventDto) {
        //читаем оригинальное событие
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с идентификатором " + eventId + " не найдено")
        );
        //проверка временных условий
        String now = TimeConverter.formatNow();
        if (TimeConverter.getDistanceInMinutes(event.getEventDate(), now) < 120) {
            throw new ConflictException("До события осталось менее двух часов");
        }
        if ((eventDto.getEventDate() != null) && TimeConverter.afterOrEquals(now, eventDto.getEventDate())) {
            throw new BadRequestException("Попытка установить прошедшую дату события");
        }
        //проверяем его состояние
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Опубликованное событие редактировать нельзя");
            //throw new BadRequestException("Опубликованное событие редактировать нельзя");
        }
        //читаем инициатора
        if (event.getInitiator().getId() != userId) { //событие чужое
            throw new BadRequestException("Чужое событие редактировать нельзя");
        }
        //читаем категорию
        Long catId = eventDto.getCategory();
        if (catId != null) {
            Category category = categoryRepository.findById(catId).orElseThrow(
                    () -> new BadRequestException("Категория " + catId + " не найдена")
            );
            event.setCategory(category);
        }
        //обновляем событие
        EventDtoMapper.patchUserDtoToEvent(eventDto, event);
        Event updatedEvent = eventRepository.save(event);
        return EventDtoMapper.toEventFullDto(updatedEvent);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsForEvent(long userId, long eventId,
                                                                EventRequestStatusUpdateRequest requestDto) {
        //читаем событие
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Не найдено событие с идентификатором " + eventId)
        );

        //Валидация запроса
        //проверяем, нужно ли подтверждение заявок
        //TODO Проверить по тестам !!!
        /*if (!event.getRequestModeration() || (event.getParticipantLimit() == 0)) { //не нужно
            throw new ConflictException("Подтверждение заявок не требуется");
        }*/
        //проверяем, есть ли свободные места
        //TODO Проверить !!!
        //if (event.getConfirmedRequests() == event.getParticipantLimit().longValue()) { //нет, ошибка
        if (event.getConfirmedRequests() + 1 == event.getParticipantLimit().longValue()) { //нет, ошибка
            throw new ConflictException("Лимит заявок исчерпан");
        }
        //создаем список идентификаторов без дублей
        List<Long> requestIds = requestDto.getRequestIds().stream()
                .distinct().collect(Collectors.toList());
        //читаем заявки с заданными идентификаторами на это событие
        List<Request> requests = requestRepository.getRequestsForEventWithIds(
                userId, eventId, requestIds);
        //все ли заявки найдены?
        if (requests.size() < requestIds.size()) { //не все
            throw new BadRequestException("Некоторые заявки из запроса не найдены");
        }
        //проверяем, все ли заявки имеют статус ожидания
        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) { //не все, ошибка
                throw new BadRequestException(
                        "Заявка " + request.getId() + " имеет статус " + request.getStatus());
            }
        }

        //Обработка запроса
        //создаем выходной объект
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                new ArrayList<>(), new ArrayList<>());
        //целевой статус
        RequestStatus targetStatus = requestDto.getStatus();
        //преобразуем список заявок в отображение
        Map<Long, Request> idToRequest = new HashMap<>();
        for (Request request : requests) {
            idToRequest.put(request.getId(), request);
        }
        //случай, когда число подтвержденных заявок неважно
        if (!event.getRequestModeration() ||
                (targetStatus == RequestStatus.REJECTED) || (event.getParticipantLimit() == 0)) {
            //просто обновляем статусы нужным образом
            processRequests(idToRequest, requestIds, targetStatus, result);
            //TODO Надо ли это?
            /*if (targetStatus == RequestStatus.CONFIRMED) {
                event.setConfirmedRequests(event.getConfirmedRequests() + requestIds.size());
            }
            eventRepository.save(event);*/
            return result;
        }
        //далее - одобрение с ограниченным числом участников
        long freePlaceCount = event.getParticipantLimit() - event.getConfirmedRequests();
        //отбираем id первых заявок (их надо одобрить)
        List<Long> idsToConfirm = requestIds.stream()
                .limit(freePlaceCount)
                .collect(Collectors.toList());
        //сохраняем их в списке подтвержденных
        processRequests(idToRequest, idsToConfirm, RequestStatus.CONFIRMED, result);
        //увеличиваем счетчик одобренных заявок события
        event.setConfirmedRequests(event.getConfirmedRequests() + idsToConfirm.size());
        //сохраняем измененное событие
        eventRepository.save(event);
        if (freePlaceCount < requestIds.size()) { //не все заявки можно удовлетворить
            //отбираем id лишних заявок (их придется отвергнуть)
            List<Long> idsToReject = requestIds.stream()
                    .skip(freePlaceCount)
                    .collect(Collectors.toList());
            //сохраняем их в списке отвергнутых
            processRequests(idToRequest, idsToReject, RequestStatus.REJECTED, result);
        }
        return result;
    }

    private void processRequests(Map<Long, Request> requests, List<Long> requestIds,
                                 RequestStatus status, EventRequestStatusUpdateResult result) {
        List<ParticipationRequestDto> list;
        if (status == RequestStatus.CONFIRMED) {
            list = result.getConfirmedRequests();
        } else {
            list = result.getRejectedRequests();
        }
        for (Long requestId : requestIds) {
            Request request = requests.get(requestId); //читаем заявки в нужном порядке
            request.setStatus(status); //выставляем нужный статус
            list.add(RequestDtoMapper.toRequestDto(request)); //сохраняем
        }
        //обновляем базу
        requestRepository.updateStatus(requestIds, status);
    }
}
