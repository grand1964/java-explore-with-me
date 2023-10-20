package ru.practicum.ewm.controller.public_api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@AllArgsConstructor
@Validated
public class PublicCategoryController {
    private CategoryService categoryService;

    //создание новой категории
    @GetMapping
    public List<CategoryDto> getCategory(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("Запрошено получение категорий");
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return categoryService.getCategories(pageable);
    }

    //обновление категории
    @GetMapping(path = "/{catId}")
    public CategoryDto getCategoryById(@PathVariable long catId) {
        log.debug("Запрошена категория с идентификатором " + catId);
        return categoryService.getCategoryById(catId);
    }
}
