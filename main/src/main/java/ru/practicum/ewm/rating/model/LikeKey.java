package ru.practicum.ewm.rating.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LikeKey implements Serializable {
    private Long user;
    private Long event;
}
