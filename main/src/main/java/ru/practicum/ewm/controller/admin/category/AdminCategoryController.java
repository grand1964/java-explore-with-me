package ru.practicum.ewm.controller.admin.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@AllArgsConstructor
public class AdminCategoryController {
    private CategoryService categoryService;

    //создание новой категории
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("Запрошено создание новой категории.");
        return ResponseEntity
                .status(201)
                .body(categoryService.createCategory(categoryDto));
    }

    //обновление категории
    @PatchMapping(path = "/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long catId,
                                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Запрошено обновление категории.");
        categoryDto.setId(catId); //прикрепляем к данным запроса идентификатор
        return ResponseEntity.ok(categoryService.updateCategory(categoryDto));
    }

    // удаление категории
    @DeleteMapping(value = "/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long catId) {
        log.info("Запрошено удаление категории с идентификатором " + catId);
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }
}
