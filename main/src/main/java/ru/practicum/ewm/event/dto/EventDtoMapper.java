package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.common.convert.TimeConverter;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class EventDtoMapper {
    public static Event toEvent(NewEventDto dto, Category category, User initiator) {
        //создаем событие
        Event event = new Event();
        //копируем обязательные поля из входного dto
        event.setTitle(dto.getTitle());
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocationLat(dto.getLocation().getLat());
        event.setLocationLon(dto.getLocation().getLon());
        //копируем необязательные поля из входного dto (или значения по умолчанию)
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        } else {
            event.setPaid(false);
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        } else {
            event.setParticipantLimit(0);
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        } else {
            event.setRequestModeration(true);
        }
        //добавляем вычисляемые поля
        event.setCreatedOn(TimeConverter.formatNow()); //дата создания
        event.setState(EventState.PENDING); //состояние ожидания
        //добавляем поля-объекты, которых нет в dto
        event.setCategory(category);
        event.setInitiator(initiator);
        //заполняем поля по умолчанию
        event.setId(null);
        event.setPublishedOn(null);
        event.setConfirmedRequests(0L);
        //возвращаем событие
        return event;
    }

    public static void patchAdminDtoToEvent(UpdateEventAdminRequest dto, Event event) {
        //копируем непустые поля из входного dto
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getLocation() != null) {
            event.setLocationLat(dto.getLocation().getLat());
            event.setLocationLon(dto.getLocation().getLon());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PENDING);
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
    }

    public static void patchUserDtoToEvent(UpdateEventUserRequest dto, Event event) {
        //копируем непустые поля из входного dto
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getLocation() != null) {
            event.setLocationLat(dto.getLocation().getLat());
            event.setLocationLon(dto.getLocation().getLon());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto dto = new EventFullDto();
        dto.copyFromEvent(event);
        return dto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto dto = new EventShortDto(event);
        dto.copyFromEvent(event);
        return dto;
    }

    /*public static void copyDtoToEvent(UpdateEventAdminRequest dto, Event event) {
        event.setTitle(dto.getTitle());
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setPaid(dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setLocationLat(dto.getLocation().getLat());
        event.setLocationLon(dto.getLocation().getLon());
        event.setRequestModeration(dto.getRequestModeration());
    }*/

    public static List<EventShortDto> toEventShortDtoList(Iterable<Event> events) {
        List<EventShortDto> list = new ArrayList<>();
        for (Event event : events) {
            list.add(toEventShortDto(event));
        }
        return list;
    }
}
