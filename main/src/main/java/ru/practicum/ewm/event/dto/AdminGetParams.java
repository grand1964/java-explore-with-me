package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminGetParams {
    private Long[] users;
    private String[] states;
    private Long[] categories;
    private String rangeStart;
    private String rangeEnd;
}
