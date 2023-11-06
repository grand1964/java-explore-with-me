package ru.practicum.ewm.rating.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.convert.ListConverter;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventFullDtoWithRating;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.rating.dto.EventWithRating;
import ru.practicum.ewm.rating.dto.UserWithRating;
import ru.practicum.ewm.rating.model.Like;
import ru.practicum.ewm.rating.model.LikeKey;
import ru.practicum.ewm.rating.model.RatingSortMode;
import ru.practicum.ewm.rating.storage.LikeRepository;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.dto.UserDtoWithRating;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {
    private LikeRepository likeRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    @Override
    public List<EventFullDtoWithRating> getEventsWithRating(RatingSortMode sortMode, Pageable pageable) {
        Page<EventWithRating> page;
        if (sortMode == RatingSortMode.SORT_DESC) {
            page = likeRepository.getEventsWithPositiveRating(pageable);
        } else {
            page = likeRepository.getEventsWithNegativeRating(pageable);
        }
        return ListConverter.toEventDtoList(page.getContent());
    }

    @Override
    public List<UserDtoWithRating> getUsersWithRating(RatingSortMode sortMode, Pageable pageable) {
        Page<UserWithRating> page;
        if (sortMode == RatingSortMode.SORT_DESC) {
            page = likeRepository.getUsersWithPositiveRating(pageable);
        } else {
            page = likeRepository.getUsersWithNegativeRating(pageable);
        }
        return ListConverter.toUserDtoList(page.getContent());
    }

    @Override
    public void createLike(long userId, long eventId, int rating) {
        //проверяем корректность идентификаторов
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с идентификатором " + userId + " не найден")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с идентификатором " + eventId + " не найдено")
        );
        //нельзя ставить лайк своему событию
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("Инициатор " + user + " пытается оценить свое событие " + eventId);
        }
        //проверяем, имеет ли пользователь одобренную заявку на событие
        if (likeRepository.countOfEventParticipationByUser(
                userId, eventId, RequestStatus.CONFIRMED) == 0) { //не имеет
            throw new ConflictException("Пользователь " + user + " не имеет одобренной заявки на событие " + eventId);
        }
        //проверяем, не ставил ли уже пользователь лайк событию
        if (likeRepository.findById(new LikeKey(user.getId(), event.getId())).isEmpty()) { //не ставил
            likeRepository.save(new Like(user, event, rating)); //сохраняем лайк
        } else { //повторно ставить нельзя
            throw new ConflictException("Пользователь " + user + " уже оценивал событие " + eventId);
        }
    }

    @Override
    public void deleteLike(long userId, long eventId) {
        //проверяем корректность идентификаторов
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с идентификатором " + userId + " не найден")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с идентификатором " + eventId + " не найдено")
        );
        Optional<Like> like = likeRepository.findById(new LikeKey(user.getId(), event.getId()));
        if (like.isPresent()) {
            likeRepository.delete(like.get());
        } else {
            throw new ConflictException("Пользователь " + user + " не оценивал событие " + eventId);
        }
    }
}
