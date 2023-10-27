package ru.practicum.ewm.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryDtoMapper;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.common.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        log.info("Получен список всех категорий");
        return categoryRepository.findAll(pageable)
                .map(CategoryDtoMapper::toCategoryDto)
                .getContent();
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория с идентификатором " + catId + " не найдена.")
        );
        return CategoryDtoMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        Category newCategory = categoryRepository.save(CategoryDtoMapper.toCategory(categoryDto));
        log.info("Создана новая категория с идентификатором " + newCategory.getId());
        return CategoryDtoMapper.toCategoryDto(newCategory);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        long id = categoryDto.getId();
        if (categoryRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Категория " + id + " не найдена и не может быть обновлена");
        }
        Category newCategory = categoryRepository.save(CategoryDtoMapper.toCategory(categoryDto));
        log.info("Обновлена категория с идентификатором " + categoryDto.getId());
        return CategoryDtoMapper.toCategoryDto(newCategory);
    }

    @Override
    public void deleteCategory(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория с идентификатором " + catId + " не найдена.")
        );
        categoryRepository.delete(category);
    }
}
