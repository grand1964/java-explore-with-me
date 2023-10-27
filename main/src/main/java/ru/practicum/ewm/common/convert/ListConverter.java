package ru.practicum.ewm.common.convert;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.event.dto.EventDtoMapper;
import ru.practicum.ewm.event.dto.EventFullDtoWithRating;
import ru.practicum.ewm.rating.dto.EventWithRating;
import ru.practicum.ewm.rating.dto.UserWithRating;
import ru.practicum.ewm.user.dto.UserDtoMapper;
import ru.practicum.ewm.user.dto.UserDtoWithRating;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ListConverter {
    public static List<EventFullDtoWithRating> toEventDtoList(List<EventWithRating> list) {
        List<EventFullDtoWithRating> dtoList = new ArrayList<>();
        for (EventWithRating dto : list) {
            EventFullDtoWithRating fullDto = EventDtoMapper.toEventFullDtoWithRating(dto.getEvent(), dto.getRating());
            dtoList.add(fullDto);
        }
        return dtoList;
    }

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
