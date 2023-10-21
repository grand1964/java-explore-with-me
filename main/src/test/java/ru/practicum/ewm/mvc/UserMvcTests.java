package ru.practicum.ewm.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.admin_api.AdminUserController;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminUserController.class)
public class UserMvcTests {
    private final long userId = 1L;
    private final NewUserRequest userVasya = new NewUserRequest("vasya@com", "Vasya");
    private final UserDto userVasyaOut = new UserDto("vasya@com", userId, "Vasya");
    private final UserDto userPetyaOut = new UserDto("petya@com", userId, "Petya");
    private final UserDto userFedyaOut = new UserDto("fedya@com", userId, "Fedya");

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    ///////////////////////////// Получение данных ///////////////////////////

    @Test
    void getUsersWithIdsTest() throws Exception {
        UserDto[] users = {userVasyaOut, userPetyaOut, userFedyaOut};
        when(userService.getUsers(any(Long[].class), any(Pageable.class)))
                .thenAnswer(invocationOnMock -> {
                            Long[] userIds = invocationOnMock.getArgument(0);
                            List<UserDto> dtos = new ArrayList<>();
                            for (long id : userIds) {
                                dtos.add(users[(int) id]);
                            }
                            return dtos;
                        }
                );

        mvc.perform(get("/admin/users?ids={ids}&from={from}&size={size}", "0,2", 0, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userVasyaOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userVasyaOut.getName())))
                .andExpect(jsonPath("$[0].email", is(userVasyaOut.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userFedyaOut.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userFedyaOut.getName())))
                .andExpect(jsonPath("$[1].email", is(userFedyaOut.getEmail())));
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getUsers(isNull(), any(Pageable.class)))
                .thenReturn(List.of(userVasyaOut, userPetyaOut, userFedyaOut));

        mvc.perform(get("/admin/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(userVasyaOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userVasyaOut.getName())))
                .andExpect(jsonPath("$[0].email", is(userVasyaOut.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userPetyaOut.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userPetyaOut.getName())))
                .andExpect(jsonPath("$[1].email", is(userPetyaOut.getEmail())))
                .andExpect(jsonPath("$[2].id", is(userFedyaOut.getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(userFedyaOut.getName())))
                .andExpect(jsonPath("$[2].email", is(userFedyaOut.getEmail())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "x"})
    void getUsersWithBadSizeTest(String value) throws Exception {
        mvc.perform(get("/admin/users?from={}&size={}", 0, value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "x"})
    void getUsersWithBadFromTest(String value) throws Exception {
        mvc.perform(get("/admin/users?from={}&size={}", value, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0;1", "{0,1}", "0,x", "x"})
    void getUsersWithBadIdsTest(String value) throws Exception {
        mvc.perform(get("/admin/users?ids={ids}", value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    //////////////////////////// Создание и удаление /////////////////////////

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any(NewUserRequest.class)))
                .thenReturn(userVasyaOut);

        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(userVasya))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id", is(userVasyaOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userVasyaOut.getName())))
                .andExpect(jsonPath("$.email", is(userVasyaOut.getEmail())));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/admin/users/{id}", userId))
                .andExpect(status().is(204));
    }
}
