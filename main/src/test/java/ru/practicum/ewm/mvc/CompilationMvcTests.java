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
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.controller.admin_api.AdminCompilationController;
import ru.practicum.ewm.controller.public_api.PublicCompilationController;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminCompilationController.class, PublicCompilationController.class})
public class CompilationMvcTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    CompilationService compilationService;

    @Autowired
    private MockMvc mvc;

    ///////////////////////////// Получение данных ///////////////////////////

    @ParameterizedTest
    @ValueSource(strings = {"-1", "x"})
    void getCompilationsWithBadFromTest(String value) throws Exception {
        mvc.perform(get("/compilations?from={}&size={}", value, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "x"})
    void getCompilationsWithBadSizeTest(String value) throws Exception {
        mvc.perform(get("/compilations?from={}&size={}", 0, value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getCompilationsWithOptionalParamsTest() throws Exception {
        CompilationDto outDto = MvcTestUtil.createCompilationDto();
        when(compilationService.getCompilations(anyBoolean(), any(Pageable.class)))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/compilations?pinned={pinned}&from={from}&size={size}",
                        true, 0, 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(17L), Long.class))
                .andExpect(jsonPath("$[0].title", is(outDto.getTitle())))
                .andExpect(jsonPath("$[0].pinned", is(outDto.getPinned())));
    }

    @Test
    void getCompilationsWithoutOptionalParamsTest() throws Exception {
        CompilationDto outDto = MvcTestUtil.createCompilationDto();
        when(compilationService.getCompilations(isNull(), any(Pageable.class)))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/compilations")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(17L), Long.class))
                .andExpect(jsonPath("$[0].title", is(outDto.getTitle())))
                .andExpect(jsonPath("$[0].pinned", is(outDto.getPinned())));
    }

    @Test
    void getCompilationByIdTest() throws Exception {
        CompilationDto outDto = MvcTestUtil.createCompilationDto();
        when(compilationService.getCompilationById(anyLong()))
                .thenReturn(outDto);

        mvc.perform(get("/compilations/{compId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(17L), Long.class))
                .andExpect(jsonPath("$.title", is(outDto.getTitle())))
                .andExpect(jsonPath("$.pinned", is(outDto.getPinned())));
    }

    //////////////////////////// Создание подборок ///////////////////////////

    @Test
    void createCompilationWithTooShortTitleTest() throws Exception {
        NewCompilationDto inDto = MvcTestUtil.createNewCompilationDto();
        inDto.setTitle("");
        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void createCompilationWithTooLongTitleTest() throws Exception {
        NewCompilationDto inDto = MvcTestUtil.createNewCompilationDto();
        inDto.setTitle(MvcTestUtil.pattern.repeat(5) + "x");
        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void normalCreateCompilationTest() throws Exception {
        NewCompilationDto inDto = MvcTestUtil.createNewCompilationDto();
        CompilationDto outDto = MvcTestUtil.createCompilationDto();
        when(compilationService.createCompilation(any(NewCompilationDto.class)))
                .thenReturn(outDto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id", is(17L), Long.class))
                .andExpect(jsonPath("$.title", is(outDto.getTitle())))
                .andExpect(jsonPath("$.pinned", is(outDto.getPinned())));
    }

    /////////////////////////// Обновление подборок ///////////////////////////

    @Test
    void updateCompilationWithTooShortTitleTest() throws Exception {
        UpdateCompilationRequest inDto = MvcTestUtil.createUpdateCompilationDto();
        inDto.setTitle("");
        mvc.perform(patch("/admin/compilations/{compId}", 1L)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void updateCompilationWithTooLongTitleTest() throws Exception {
        UpdateCompilationRequest inDto = MvcTestUtil.createUpdateCompilationDto();
        inDto.setTitle(MvcTestUtil.pattern.repeat(5) + "x");
        mvc.perform(patch("/admin/compilations/{compId}", 1L)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void normalUpdateCompilationTest() throws Exception {
        UpdateCompilationRequest inDto = MvcTestUtil.createUpdateCompilationDto();
        CompilationDto outDto = MvcTestUtil.createCompilationDto();
        when(compilationService.updateCompilation(anyLong(), any(UpdateCompilationRequest.class)))
                .thenReturn(outDto);

        mvc.perform(patch("/admin/compilations/{compId}", 1L)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(17L), Long.class))
                .andExpect(jsonPath("$.title", is(outDto.getTitle())))
                .andExpect(jsonPath("$.pinned", is(outDto.getPinned())));
    }

    //////////////////////////// Удаление подборок ////////////////////////////

    @Test
    void deleteCompilationWithBadIdTest() throws Exception {
        mvc.perform(delete("/admin/compilations/{compId}", "x")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void normalDeleteCompilationTest() throws Exception {
        mvc.perform(delete("/admin/compilations/{compId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }
}
