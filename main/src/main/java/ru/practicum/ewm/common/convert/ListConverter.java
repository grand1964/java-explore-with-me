package ru.practicum.ewm.common.convert;

import ru.practicum.ewm.event.dto.EventDtoMapper;
import ru.practicum.ewm.event.dto.EventFullDtoWithRating;
import ru.practicum.ewm.rating.dto.EventWithRating;
import ru.practicum.ewm.rating.dto.UserWithRating;
import ru.practicum.ewm.user.dto.UserDtoMapper;
import ru.practicum.ewm.user.dto.UserDtoWithRating;

import java.util.ArrayList;
import java.util.List;

public class ListConverter {
    /*public static List<EventFullDtoWithRating> toEventDtoList(List<PairToReturn<Event, Long>> list) {
        List<EventFullDtoWithRating> dtoList = new ArrayList<>();
        for (PairToReturn<Event, Long> entry : list) {
            EventFullDtoWithRating dto = (EventFullDtoWithRating) EventDtoMapper.toEventFullDto(entry.getKey());
            dto.setRating(entry.getValue());
            dtoList.add(dto);
        }
        return dtoList;
    }*/

    public static List<EventFullDtoWithRating> toEventDtoList(List<EventWithRating> list) {
        List<EventFullDtoWithRating> dtoList = new ArrayList<>();
        for (EventWithRating dto : list) {
            EventFullDtoWithRating fullDto = EventDtoMapper.toEventFullDtoWithRating(dto.getEvent(), dto.getRating());
            dtoList.add(fullDto);
        }
        return dtoList;
    }

    /*public static List<UserDtoWithRating> toUserDtoList(List<PairToReturn<User, Long>> list) {
        List<UserDtoWithRating> dtoList = new ArrayList<>();
        for (PairToReturn<User, Long> entry : list) {
            UserDtoWithRating dto = UserDtoMapper.toUserDtoWithRating(entry.getKey());
            dto.setRating(entry.getValue());
            dtoList.add(dto);
        }
        return dtoList;
    }*/

    public static List<UserDtoWithRating> toUserDtoList(List<UserWithRating> list) {
        List<UserDtoWithRating> dtoList = new ArrayList<>();
        for (UserWithRating dto : list) {
            UserDtoWithRating fullDto = UserDtoMapper.toUserDtoWithRating(dto.getUser());
            fullDto.setRating(dto.getRating());
            dtoList.add(fullDto);
        }
        return dtoList;
    }
}
