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
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.controller.admin_api.AdminCategoryController;
import ru.practicum.ewm.controller.public_api.PublicCategoryController;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminCategoryController.class, PublicCategoryController.class})
public class CategoryMvcTests {
    private final CategoryDto categoryDto = new CategoryDto(1L, "Категория");
    private final CategoryDto categoryDto1 = new CategoryDto(2L, "Категория 1");
    private final CategoryDto categoryDto2 = new CategoryDto(3L, "Категория 2");

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CategoryService categoryService;

    @Autowired
    private MockMvc mvc;

    ///////////////////////////// Получение данных ///////////////////////////

    @Test
    void getCategoriesTest() throws Exception {
        when(categoryService.getCategories(any(Pageable.class)))
                .thenReturn(List.of(categoryDto, categoryDto1, categoryDto2));

        mvc.perform(get("/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(categoryDto.getName())))
                .andExpect(jsonPath("$[1].id", is(categoryDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(categoryDto1.getName())))
                .andExpect(jsonPath("$[2].id", is(categoryDto2.getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(categoryDto2.getName())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "x"})
    void getCategoriesWithBadSizeTest(String value) throws Exception {
        mvc.perform(get("/categories?from={}&size={}", 0, value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "x"})
    void getCategoriesWithBadFromTest(String value) throws Exception {
        mvc.perform(get("/categories?from={}&size={}", value, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getCategoryTest() throws Exception {
        when(categoryService.getCategoryById(1L))
                .thenReturn(categoryDto);

        mvc.perform(get("/categories/{catId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    //////////////////////////// Создание и удаление /////////////////////////
    @Test
    void createCategoryTest() throws Exception {
        when(categoryService.createCategory(any(NewCategoryDto.class)))
                .thenReturn(categoryDto);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    void updateCategoryTest() throws Exception {
        when(categoryService.updateCategory(any(CategoryDto.class)))
                .thenReturn(categoryDto);

        mvc.perform(patch("/admin/categories/{catId}", 1L)
                        .content(mapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    void deleteCategoryTest() throws Exception {
        mvc.perform(delete("/admin/categories/{catId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }
}
