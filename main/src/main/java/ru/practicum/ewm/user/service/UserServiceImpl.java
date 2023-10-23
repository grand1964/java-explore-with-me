package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        if (ids == null) { //идентификаторы не заданы
            log.debug("Получен список всех пользователей");
            return userRepository.findAll(pageable)
                    .map(UserDtoMapper::toUserDto)
                    .getContent();
        } else { //идентификаторы заданы, выводим без пагинации
            log.debug("Получен список пользователей с заданными идентификаторами");
            //получаем пользователей из базы
            return UserDtoMapper.toUserDtoList(userRepository.findUsersForIds(ids));
        }
    }

    @Override
    public UserDto createUser(NewUserRequest userRequest) {
        User newUser = userRepository.save(UserDtoMapper.toUser(userRequest));
        log.info("Создан новый пользователь с идентификатором " + newUser.getId());
        return UserDtoMapper.toUserDto(newUser);
    }

    @Override
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с идентификатором " + userId + " не найден")
        );
        userRepository.delete(user);
        log.info("Пользователь с идентификатором " + userId + " удален");
    }
}
