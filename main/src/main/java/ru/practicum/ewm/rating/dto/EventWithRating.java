package ru.practicum.ewm.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.model.Event;

@Getter
@Setter
@AllArgsConstructor
public class EventWithRating {
    private Event event;
    private Long rating;
}
