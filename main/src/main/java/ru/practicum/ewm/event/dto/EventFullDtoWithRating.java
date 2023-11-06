package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventFullDtoWithRating extends EventFullDto {
    private long rating;
}
