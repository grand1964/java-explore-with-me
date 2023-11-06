package ru.practicum.ewm.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.user.model.User;

@Getter
@Setter
@AllArgsConstructor
public class UserWithRating {
    private User user;
    private Long rating;
}
