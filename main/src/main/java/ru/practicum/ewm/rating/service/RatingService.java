package ru.practicum.ewm.rating.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.EventFullDtoWithRating;
import ru.practicum.ewm.rating.model.RatingSortMode;
import ru.practicum.ewm.user.dto.UserDtoWithRating;

import java.util.List;

public interface RatingService {
    List<EventFullDtoWithRating> getEventsWithRating(RatingSortMode sortMode, Pageable pageable);

    List<UserDtoWithRating> getUsersWithRating(RatingSortMode sortMode, Pageable pageable);

    void createLike(long userId, long eventId, int rating);

    void deleteLike(long userId, long eventId);
}
