package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryDtoMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserDtoMapper;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@NoArgsConstructor
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;

    public EventShortDto(Event event) {
        this(); //конструктор без параметров
        copyFromEvent(event);
    }

    public void copyFromEvent(Event event) {
        id = event.getId();
        title = event.getTitle();
        annotation = event.getAnnotation();
        eventDate = event.getEventDate();
        category = CategoryDtoMapper.toCategoryDto(event.getCategory());
        paid = event.getPaid();
        confirmedRequests = event.getConfirmedRequests();
        initiator = UserDtoMapper.toUserShortDto(event.getInitiator());
        views = 0L;
    }
}
