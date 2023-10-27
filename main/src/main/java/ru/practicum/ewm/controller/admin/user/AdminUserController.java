package ru.practicum.ewm.controller.admin.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
@Validated
public class AdminUserController {
    private UserService userService;

    //получение пользователей
    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Long[] ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("Запрошено получение пользователей");
        List<Long> idsList = null;
        if (ids != null) {
            idsList = new ArrayList<>();
            Collections.addAll(idsList, ids);
        }
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return userService.getUsers(idsList, pageable);
    }

    // создание нового пользователя
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequest userRequest) {
        log.info("Запрошено создание нового пользователя");
        return ResponseEntity
                .status(201)
                .body(userService.createUser(userRequest));
    }

    // удаление пользователя
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        log.info("Запрошено удаление пользователя с идентификатором " + userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }
}
