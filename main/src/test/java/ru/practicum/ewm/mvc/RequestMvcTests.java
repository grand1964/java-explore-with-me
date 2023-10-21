package ru.practicum.ewm.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.private_api.PrivateRequestController;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PrivateRequestController.class})
public class RequestMvcTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mvc;

    ///////////////////////////// Получение данных ///////////////////////////

    @Test
    void getNormalUserRequestsTest() throws Exception {
        ParticipationRequestDto outDto = MvcTestUtil.createRequestDto();
        when(requestService.getUserRequests(anyLong()))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/users/{userId}/requests", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].event", is(outDto.getEvent()), Long.class))
                .andExpect(jsonPath("$[0].requester", is(outDto.getRequester()), Long.class))
                .andExpect(jsonPath("$[0].created", is(outDto.getCreated())))
                .andExpect(jsonPath("$[0].status", is(outDto.getStatus())));
    }

    ///////////////////////////// Создание запроса ///////////////////////////

    @ParameterizedTest
    @ValueSource(strings = {" ", "x"})
    void createRequestWithBadUserIsTest(String value) throws Exception {
        ParticipationRequestDto outDto = MvcTestUtil.createRequestDto();
        mvc.perform(post("/users/{userId}/requests?eventId={eventId}", 1, value)
                        .content(mapper.writeValueAsString(outDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void normalCreateRequestTest() throws Exception {
        ParticipationRequestDto outDto = MvcTestUtil.createRequestDto();
        when(requestService.createRequest(anyLong(), anyLong()))
                .thenReturn(outDto);

        mvc.perform(post("/users/{userId}/requests?eventId={eventId}", 1, 17)
                        .content(mapper.writeValueAsString(outDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(outDto.getEvent()), Long.class))
                .andExpect(jsonPath("$.requester", is(outDto.getRequester()), Long.class))
                .andExpect(jsonPath("$.created", is(outDto.getCreated())))
                .andExpect(jsonPath("$.status", is(outDto.getStatus())));
    }

    //////////////////////////// Обновление запроса //////////////////////////

    @Test
    void cancelRequestTest() throws Exception {
        ParticipationRequestDto outDto = MvcTestUtil.createRequestDto();
        when(requestService.cancelRequest(anyLong(), anyLong()))
                .thenReturn(outDto);

        mvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", 1, 17)
                        .content(mapper.writeValueAsString(outDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(outDto.getEvent()), Long.class))
                .andExpect(jsonPath("$.requester", is(outDto.getRequester()), Long.class))
                .andExpect(jsonPath("$.created", is(outDto.getCreated())))
                .andExpect(jsonPath("$.status", is(outDto.getStatus())));
    }
}
