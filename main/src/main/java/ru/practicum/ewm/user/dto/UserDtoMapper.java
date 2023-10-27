package ru.practicum.ewm.user.dto;

import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDtoMapper {
    public static User toUser(NewUserRequest userRequest) {
        return new User(null, userRequest.getName(), userRequest.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getEmail(), user.getId(), user.getName());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public static List<UserDto> toUserDtoList(List<User> userList) {
        return userList.stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
