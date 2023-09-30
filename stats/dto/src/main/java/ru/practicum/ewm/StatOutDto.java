package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatOutDto {
    String app;
    String uri;
    Long hits;
}
