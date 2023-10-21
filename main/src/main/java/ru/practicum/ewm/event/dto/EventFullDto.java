package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryDtoMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserDtoMapper;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long views;

    public void copyFromEvent(Event event) {
        //копия того, что в EventShortDto
        id = event.getId();
        title = event.getTitle();
        annotation = event.getAnnotation();
        eventDate = event.getEventDate();
        category = CategoryDtoMapper.toCategoryDto(event.getCategory());
        paid = event.getPaid();
        confirmedRequests = event.getConfirmedRequests();
        initiator = UserDtoMapper.toUserShortDto(event.getInitiator());
        views = 0L;
        //а теперь - то, чего нет в EventShortDto
        description = event.getDescription();
        createdOn = event.getCreatedOn();
        publishedOn = event.getPublishedOn();
        participantLimit = event.getParticipantLimit();
        location = new Location(event.getLocationLat(), event.getLocationLon());
        state = event.getState();
        requestModeration = event.getRequestModeration();
    }
}
