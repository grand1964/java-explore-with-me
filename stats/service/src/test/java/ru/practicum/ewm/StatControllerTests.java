package ru.practicum.ewm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.StatController;
import ru.practicum.ewm.service.StatService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatController.class)
public class StatControllerTests {
    private static StatInDto statInDto;
    private static StatOutDto statOutDto;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    StatService service;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    public static void setUp() {
        //создаем входной объект-статистику
        statInDto = new StatInDto(
                "ewm-main-service", "/events/1", "192.163.0.1", "2022-09-06 11:00:23");
        //создаем выходной объект-статистику
        statOutDto = new StatOutDto("ewm-main-service", "/events/1", 17L);
    }

    ///////////////////////////// Получение данных ///////////////////////////

    @Test
    void normalGetStatTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        when(service.getStat(anyString(), anyString(), any(String[].class), anyBoolean()))
                .thenReturn(List.of(statOutDto));

        String[] uris = {"xxx", "yyy"};
        mvc.perform(get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        "2023-10-17 00:00:00", "2023-10-18 00:00:00", uris, "false")
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].app", is(statInDto.getApp()), String.class))
                .andExpect(jsonPath("$[0].uri", is(statInDto.getUri())))
                .andExpect(jsonPath("$[0].hits", is(statOutDto.getHits().intValue())));
    }

    @Test
    void normalGetStatWithUrisAnalysisTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        when(service.getStat(anyString(), anyString(), any(String[].class), anyBoolean()))
                .thenAnswer(invocationOnMock -> {
                            String[] uris = invocationOnMock.getArgument(2, String[].class);
                            List<StatOutDto> dtos = new ArrayList<>();
                            for (String uri : uris) {
                                dtos.add(new StatOutDto("ewm-main-service", uri, 17L));
                            }
                            return dtos;
                        }
                );

        String[] uris = {"xxx", "yyy", "zzz"};
        mvc.perform(get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        "2023-10-17 00:00:00", "2023-10-18 00:00:00", uris, "false")
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].uri", is(uris[0])))
                .andExpect(jsonPath("$[1].uri", is(uris[1])))
                .andExpect(jsonPath("$[2].uri", is(uris[2])));
    }

    @Test
    void normalGetStatWithVoidUrisTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        when(service.getStat(anyString(), anyString(), any(String[].class), anyBoolean()))
                .thenAnswer(invocationOnMock -> {
                            String[] uris = invocationOnMock.getArgument(2, String[].class);
                            List<StatOutDto> dtos = new ArrayList<>();
                            for (String uri : uris) {
                                dtos.add(new StatOutDto("ewm-main-service", uri, 17L));
                            }
                            return dtos;
                        }
                );

        String[] uris = {};
        mvc.perform(get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        "2023-10-17 00:00:00", "2023-10-18 00:00:00", uris, "false")
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/stats?start={start}&end={end}&uris=&unique={unique}",
                        "2023-10-17 00:00:00", "2023-10-18 00:00:00", "false")
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getStatWithoutUniqueTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        when(service.getStat(anyString(), anyString(), any(String[].class), anyBoolean()))
                .thenReturn(List.of(statOutDto));

        String[] uris = {"xxx", "yyy"};
        mvc.perform(get("/stats?start={start}&end={end}&uris={uris}",
                        "2023-10-17 00:00:00", "2023-10-18 00:00:00", uris)
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].app", is(statInDto.getApp()), String.class))
                .andExpect(jsonPath("$[0].uri", is(statInDto.getUri())))
                .andExpect(jsonPath("$[0].hits", is(statOutDto.getHits().intValue())));
    }

    @Test
    void getStatWithoutUrisTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        when(service.getStat(anyString(), anyString(), eq(null), anyBoolean()))
                .thenReturn(List.of(statOutDto));

        mvc.perform(get("/stats?start={start}&end={end}&unique={unique}",
                        "2023-10-17 00:00:00", "2023-10-18 00:00:00", "false")
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].app", is(statInDto.getApp()), String.class))
                .andExpect(jsonPath("$[0].uri", is(statInDto.getUri())))
                .andExpect(jsonPath("$[0].hits", is(statOutDto.getHits().intValue())));
    }

    @Test
    void getStatWithoutStartTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        when(service.getStat(anyString(), anyString(), any(String[].class), anyBoolean()))
                .thenReturn(List.of(statOutDto));

        mvc.perform(get("/stats?end={end}", "bbb")
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getStatWithoutEndTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        when(service.getStat(anyString(), anyString(), any(String[].class), anyBoolean()))
                .thenReturn(List.of(statOutDto));

        mvc.perform(get("/stats?start={start}", "aaa")
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    //////////////////////////// Сохранение данных ///////////////////////////

    @Test
    void putStatTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(statInDto))
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));
    }

    @Test
    void putStatWithBadBodyTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(null))
                        .headers(headers)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
}
