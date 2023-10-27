package ru.practicum.ewm.controller.priv.rating;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.rating.model.RatingSortMode;
import ru.practicum.ewm.rating.service.RatingService;
import ru.practicum.ewm.user.dto.UserDtoWithRating;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class PrivateRatingController {
    private RatingService ratingService;

    ////////////////////// Запросы рейтинга инициаторов //////////////////////

    @GetMapping("/ratings")
    public List<UserDtoWithRating> getUsers(@RequestParam(defaultValue = "SORT_DESC") RatingSortMode sortMode,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("Запрошено получение рейтинга инициаторов");
        PageRequest pageable = PageRequest.of(from / size, size);
        return ratingService.getUsersWithRating(sortMode, pageable);
    }

    ///////////////////////// Создание оценки события ////////////////////////

    @PostMapping("/{userId}/event/{eventId}/likes")
    public ResponseEntity<Void> createLike(@PathVariable long userId,
                                           @PathVariable long eventId,
                                           @RequestParam(defaultValue = "like") String ratingType) {
        log.info("Запрошено создание новой оценки события");
        int rating;
        switch (ratingType) {
            case "like":
                rating = 1;
                break;
            case "dislike":
                rating = -1;
                break;
            default:
                throw new BadRequestException("Неверный тип оценки: " + ratingType);
        }
        ratingService.createLike(userId, eventId, rating);
        return new ResponseEntity<>(HttpStatus.valueOf(201));
    }

    ///////////////////////// Удаление оценки события ////////////////////////

    @DeleteMapping(path = "/{userId}/event/{eventId}/likes")
    public ResponseEntity<Void> updateEventByUser(@PathVariable long userId,
                                          @PathVariable long eventId) {
        log.info("Запрошено удаление новой оценки события");
        ratingService.deleteLike(userId, eventId);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }
}
