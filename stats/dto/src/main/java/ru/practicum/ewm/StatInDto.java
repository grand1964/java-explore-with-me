package ru.practicum.ewm;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatInDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
