package ru.practicum.ewm.controller.admin.rating;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDtoWithRating;
import ru.practicum.ewm.rating.model.RatingSortMode;
import ru.practicum.ewm.rating.service.RatingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/ratings")
@AllArgsConstructor
@Validated
public class AdminRatingController {
    private RatingService ratingService;

    //запрос событий по параметрам
    @GetMapping
    public List<EventFullDtoWithRating> getEvents(@RequestParam(defaultValue = "SORT_DESC") RatingSortMode sortMode,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("Администратором запрошено получение списка событий с рейтингами");
        PageRequest pageable = PageRequest.of(from / size, size);
        return ratingService.getEventsWithRating(sortMode, pageable);
    }
}
