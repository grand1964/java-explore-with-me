package ru.practicum.ewm.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.common.stat.ClientStatService;
import ru.practicum.ewm.controller.admin.event.AdminEventController;
import ru.practicum.ewm.controller.priv.event.PrivateEventController;
import ru.practicum.ewm.controller.pub.event.PublicEventController;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminEventController.class, PrivateEventController.class, PublicEventController.class})
public class EventMvcTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    EventService eventService;

    @MockBean
    ClientStatService clientStatService;

    @Autowired
    private MockMvc mvc;

    //////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Получение данных ///////////////////////////
    //////////////////////////////////////////////////////////////////////////

    ///////////////////////////// Административное ///////////////////////////

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "x"})
    void getEventsByAdminWithBadSizeTest(String value) throws Exception {
        mvc.perform(get("/admin/events?from={}&size={}", 0, value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "x"})
    void getEventsByAdminWithBadFromTest(String value) throws Exception {
        mvc.perform(get("/admin/events?from={}&size={}", value, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getNormalEventsByAdminWithOptionalParamsTest() throws Exception {
        Long[] userIds = {0L};
        String[] stateValues = {"s"};
        Long[] categoryIds = {0L};
        NewEventDto inDto = MvcTestUtil.createNewEventDto();
        when(eventService.getEventsByAdmin(any(AdminGetParams.class), any(Pageable.class)))
                .thenReturn(List.of(MvcTestUtil.copyEventFullDto(inDto)));

        mvc.perform(get("/admin/events?users={users}&states={states}" +
                                "&categories={categories}&rangeStart={rangeStart}&rangeEnd={rangeEnd}",
                        userIds, stateValues, categoryIds, "2007-09-06 00:11:22", "2017-09-06 00:11:22")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(17L), Long.class))
                .andExpect(jsonPath("$[0].title", is(inDto.getTitle())))
                .andExpect(jsonPath("$[0].annotation", is(inDto.getAnnotation())))
                .andExpect(jsonPath("$[0].description", is(inDto.getDescription())))
                .andExpect(jsonPath("$[0].location.lat", is(inDto.getLocation().getLat()), Float.class))
                .andExpect(jsonPath("$[0].location.lon", is(inDto.getLocation().getLon()), Float.class))
                .andExpect(jsonPath("$[0].eventDate", is(inDto.getEventDate())));
    }

    @Test
    void getNormalEventsByAdminWithoutOptionalParamsTest() throws Exception {
        NewEventDto inDto = MvcTestUtil.createNewEventDto();
        when(eventService.getEventsByAdmin(any(AdminGetParams.class), any(PageRequest.class)))
                .thenReturn(List.of(MvcTestUtil.copyEventFullDto(inDto)));

        mvc.perform(get("/admin/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(17L), Long.class))
                .andExpect(jsonPath("$[0].title", is(inDto.getTitle())))
                .andExpect(jsonPath("$[0].annotation", is(inDto.getAnnotation())))
                .andExpect(jsonPath("$[0].description", is(inDto.getDescription())))
                .andExpect(jsonPath("$[0].eventDate", is(inDto.getEventDate())));
    }

    //////////////////////////////// Публичное ///////////////////////////////

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "x"})
    void searchEventsWithBadSizeTest(String value) throws Exception {
        mvc.perform(get("/events?from={}&size={}", 0, value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "x"})
    void searchEventsWithBadFromTest(String value) throws Exception {
        mvc.perform(get("/events?from={}&size={}", value, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void normalSearchEventsWithOptionalParamsTest() throws Exception {
        Long[] categoryIds = {0L};
        EventShortDto outDto = MvcTestUtil.createEventShortDto();
        when(eventService.searchEvents(any(PublicGetParams.class), anyInt(), anyInt()))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/events?text={text}&categories={categories}&paid={paid}" +
                                "&rangeStart={rangeStart}&rangeEnd={rangeEnd}&onlyAvailable={onlyAvailable}&sort={sort}",
                        "x", categoryIds, false, "2007-09-06 00:11:22", "2017-09-06 00:11:22", false, "EVENT_DATE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(17L), Long.class))
                .andExpect(jsonPath("$[0].title", is(outDto.getTitle())))
                .andExpect(jsonPath("$[0].annotation", is(outDto.getAnnotation())))
                .andExpect(jsonPath("$[0].eventDate", is(outDto.getEventDate())));
    }

    @Test
    void normalSearchEventsWithoutOptionalParamsTest() throws Exception {
        EventShortDto outDto = MvcTestUtil.createEventShortDto();
        when(eventService.searchEvents(any(PublicGetParams.class), anyInt(), anyInt()))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/events?onlyAvailable={onlyAvailable}",
                        false)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(17L), Long.class))
                .andExpect(jsonPath("$[0].title", is(outDto.getTitle())))
                .andExpect(jsonPath("$[0].annotation", is(outDto.getAnnotation())))
                .andExpect(jsonPath("$[0].eventDate", is(outDto.getEventDate())));
    }

    @Test
    void getEventByIdWithBadIdTest() throws Exception {
        mvc.perform(get("/events/{eventId}", "x")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getEventByIdTest() throws Exception {
        EventFullDto outDto = MvcTestUtil.copyEventFullDto(MvcTestUtil.createNewEventDto());
        when(eventService.getEventById(anyLong()))
                .thenReturn(outDto);

        mvc.perform(get("/events/{eventId}", 17L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(17L), Long.class))
                .andExpect(jsonPath("$.title", is(outDto.getTitle())))
                .andExpect(jsonPath("$.annotation", is(outDto.getAnnotation())))
                .andExpect(jsonPath("$.description", is(outDto.getDescription())))
                .andExpect(jsonPath("$.eventDate", is(outDto.getEventDate())));
    }

    /////////////////////////////// Приватное ////////////////////////////////

    @Test
    void getEventsByUserWithBadIdTest() throws Exception {
        mvc.perform(get("/users/{userId}/events", "x")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "x"})
    void getEventsByUserWithBadSizeTest(String value) throws Exception {
        mvc.perform(get("/users/{userId}/events?from={}&size={}", 17L, 0, value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "x"})
    void getEventsByUserWithBadFromTest(String value) throws Exception {
        mvc.perform(get("/users/{userId}/events?from={}&size={}", 17L, value, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void normalGetEventsByUserTest() throws Exception {
        EventShortDto outDto = MvcTestUtil.createEventShortDto();
        when(eventService.getEventsByUser(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/users/{userId}/events", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(17L), Long.class))
                .andExpect(jsonPath("$[0].title", is(outDto.getTitle())))
                .andExpect(jsonPath("$[0].annotation", is(outDto.getAnnotation())))
                .andExpect(jsonPath("$[0].eventDate", is(outDto.getEventDate())));
    }

    @Test
    void getEventWithIdByUserTest() throws Exception {
        EventFullDto outDto = MvcTestUtil.copyEventFullDto(MvcTestUtil.createNewEventDto());
        when(eventService.getEventWithIdByUser(anyLong(), anyLong()))
                .thenReturn(outDto);

        mvc.perform(get("/users/{userId}/events/{eventId}", 1L, 17L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(17L), Long.class))
                .andExpect(jsonPath("$.title", is(outDto.getTitle())))
                .andExpect(jsonPath("$.annotation", is(outDto.getAnnotation())))
                .andExpect(jsonPath("$.description", is(outDto.getDescription())))
                .andExpect(jsonPath("$.eventDate", is(outDto.getEventDate())));
    }

    @Test
    void getRequestsForEventTest() throws Exception {
        ParticipationRequestDto outDto = MvcTestUtil.createRequestDto();
        when(eventService.getRequestsForEvent(anyLong(), anyLong()))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/users/{userId}/events/{eventId}/requests", 1L, 13L)
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

    //////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Создание события ///////////////////////////
    //////////////////////////////////////////////////////////////////////////

    @Test
    void normalCreateEventTest() throws Exception {
        NewEventDto inDto = MvcTestUtil.createNewEventDto();
        when(eventService.createEvent(anyLong(), any(NewEventDto.class)))
                .thenReturn(MvcTestUtil.copyEventFullDto(inDto));

        mvc.perform(post("/users/{userId}/events", 1)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id", is(17L), Long.class))
                .andExpect(jsonPath("$.title", is(inDto.getTitle())))
                .andExpect(jsonPath("$.annotation", is(inDto.getAnnotation())))
                .andExpect(jsonPath("$.description", is(inDto.getDescription())))
                .andExpect(jsonPath("$.location.lat", is(inDto.getLocation().getLat()), Float.class))
                .andExpect(jsonPath("$.location.lon", is(inDto.getLocation().getLon()), Float.class))
                .andExpect(jsonPath("$.eventDate", is(inDto.getEventDate())));
    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////// Обновление события //////////////////////////
    //////////////////////////////////////////////////////////////////////////

    ///////////////////////////// Административное ///////////////////////////

    @ParameterizedTest
    @ValueSource(strings = {" ", "xx"})
    void updateEventByAdminWithTooShortTitleTest(String value) throws Exception {
        UpdateEventAdminRequest inDto = MvcTestUtil.createEventAdminRequest();
        inDto.setTitle(value);
        mvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void updateEventByAdminWithTooLongTitleTest() throws Exception {
        UpdateEventAdminRequest inDto = MvcTestUtil.createEventAdminRequest();
        inDto.setTitle(MvcTestUtil.pattern.repeat(12) + "x");
        mvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "013456789012345678"})
    void updateEventByAdminWithTooShortAnnotationTest(String value) throws Exception {
        UpdateEventAdminRequest inDto = MvcTestUtil.createEventAdminRequest();
        inDto.setAnnotation(value);
        mvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void updateEventByAdminWithTooLongAnnotationTest() throws Exception {
        UpdateEventAdminRequest inDto = MvcTestUtil.createEventAdminRequest();
        inDto.setAnnotation(MvcTestUtil.pattern.repeat(200) + "x");
        mvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "013456789012345678"})
    void updateEventByAdminWithTooShortDescriptionTest(String value) throws Exception {
        UpdateEventAdminRequest inDto = MvcTestUtil.createEventAdminRequest();
        inDto.setDescription(value);
        mvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void updateEventByAdminWithTooLongDescriptionTest() throws Exception {
        UpdateEventAdminRequest inDto = MvcTestUtil.createEventAdminRequest();
        inDto.setDescription(MvcTestUtil.pattern.repeat(700) + "x");
        mvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void normalUpdateEventByAdminTest() throws Exception {
        UpdateEventAdminRequest inDto = MvcTestUtil.createEventAdminRequest();
        when(eventService.updateEventByAdmin(anyLong(), any(UpdateEventAdminRequest.class)))
                .thenReturn(MvcTestUtil.copyEventFullDto(inDto));

        mvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(17L), Long.class))
                .andExpect(jsonPath("$.title", is(inDto.getTitle())))
                .andExpect(jsonPath("$.annotation", is(inDto.getAnnotation())))
                .andExpect(jsonPath("$.description", is(inDto.getDescription())))
                .andExpect(jsonPath("$.eventDate", is(inDto.getEventDate())));
    }

    ///////////////////////////////// Приватное //////////////////////////////

    @ParameterizedTest
    @ValueSource(strings = {" ", "xx"})
    void updateEventByUserWithTooShortTitleTest(String value) throws Exception {
        UpdateEventUserRequest inDto = MvcTestUtil.createEventUserRequest();
        inDto.setTitle(value);
        mvc.perform(patch("/users/{userId}/events/{eventId}", 1, 17)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void updateEventByUserWithTooLongTitleTest() throws Exception {
        UpdateEventUserRequest inDto = MvcTestUtil.createEventUserRequest();
        inDto.setTitle(MvcTestUtil.pattern.repeat(12) + "x");
        mvc.perform(patch("/users/{userId}/events/{eventId}", 1, 17)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "013456789012345678"})
    void updateEventByUserWithTooShortAnnotationTest(String value) throws Exception {
        UpdateEventUserRequest inDto = MvcTestUtil.createEventUserRequest();
        inDto.setAnnotation(value);
        mvc.perform(patch("/users/{userId}/events/{eventId}", 1, 17)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void updateEventByUserWithTooLongAnnotationTest() throws Exception {
        UpdateEventUserRequest inDto = MvcTestUtil.createEventUserRequest();
        inDto.setAnnotation(MvcTestUtil.pattern.repeat(200) + "x");
        mvc.perform(patch("/users/{userId}/events/{eventId}", 1, 17)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "013456789012345678"})
    void updateEventByUserWithTooShortDescriptionTest(String value) throws Exception {
        UpdateEventUserRequest inDto = MvcTestUtil.createEventUserRequest();
        inDto.setDescription(value);
        mvc.perform(patch("/users/{userId}/events/{eventId}", 1, 17)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void updateEventByUserWithTooLongDescriptionTest() throws Exception {
        UpdateEventUserRequest inDto = MvcTestUtil.createEventUserRequest();
        inDto.setDescription(MvcTestUtil.pattern.repeat(700) + "x");
        mvc.perform(patch("/users/{userId}/events/{eventId}", 1, 17)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void normalUpdateEventByUserTest() throws Exception {
        UpdateEventUserRequest inDto = MvcTestUtil.createEventUserRequest();
        when(eventService.updateEventByUser(anyLong(), anyLong(), any(UpdateEventUserRequest.class)))
                .thenReturn(MvcTestUtil.copyEventFullDto(inDto));

        mvc.perform(patch("/users/{userId}/events/{eventId}", 1, 17)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(17L), Long.class))
                .andExpect(jsonPath("$.title", is(inDto.getTitle())))
                .andExpect(jsonPath("$.annotation", is(inDto.getAnnotation())))
                .andExpect(jsonPath("$.description", is(inDto.getDescription())))
                .andExpect(jsonPath("$.location.lat", is(inDto.getLocation().getLat()), Float.class))
                .andExpect(jsonPath("$.location.lon", is(inDto.getLocation().getLon()), Float.class))
                .andExpect(jsonPath("$.eventDate", is(inDto.getEventDate())));
    }

    @Test
    void normalUpdateRequestForEventTest() throws Exception {
        EventRequestStatusUpdateRequest inDto = MvcTestUtil.createStatusUpdateRequest();
        EventRequestStatusUpdateResult outDto = MvcTestUtil.createStatusUpdateResul();
        when(eventService.updateRequestsForEvent(anyLong(), anyLong(), any(EventRequestStatusUpdateRequest.class)))
                .thenReturn(outDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}/requests", 1, 17)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests", hasSize(1)))
                .andExpect(jsonPath("$.rejectedRequests", hasSize(1)))
                .andExpect(jsonPath("$.confirmedRequests[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$.rejectedRequests[0].id", is(2L), Long.class));
    }
}
