package ru.practicum.ewm.common.stat;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

public interface ClientStatService {
    void setEvent(String uri, String ip);

    void addStatToEvents(List<EventShortDto> events);

    void addStatToEvent(EventFullDto event);
}
