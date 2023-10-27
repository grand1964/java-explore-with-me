package ru.practicum.ewm.request.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private String created; //время заявки
    private Long event; //id события
    private Long id; //id заявки
    private Long requester; //id заявителя
    private String status; //статус заявки
}
