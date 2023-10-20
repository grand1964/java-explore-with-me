package ru.practicum.ewm.common.stat;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

public interface ClientStatService {
    void setEvent(String uri, String ip);

    List<EventShortDto> getStat(List<EventShortDto> events);

    EventFullDto getStat(EventFullDto event);
}
