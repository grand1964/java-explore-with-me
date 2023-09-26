package ru.practicum.ewm;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatInDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}
