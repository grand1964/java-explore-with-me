package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(Long[] ids, Pageable pageable);

    UserDto createUser(NewUserRequest userRequest);

    void deleteUser(long userId);
}
