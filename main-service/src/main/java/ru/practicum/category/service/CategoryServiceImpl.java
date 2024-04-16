package ru.practicum.category.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Создать новую категорию");

        Category category = CategoryMapper.toCategory(newCategoryDto);

        try {
            Category savedCategory = repository.save(category);

            log.info("Создана категория {}", savedCategory.getName());

            return CategoryMapper.toCategoryDto(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Название категории должно быть уникальным");
        }
    }

    @Override
    public CategoryDto updateCategory(long categoryId, CategoryDto categoryDto) {
        log.info("Обновить категорию с id: {}", categoryId);

        Category category = validCategory(categoryId);
        category.setName(categoryDto.getName());

        try {
            Category savedCategory = repository.save(category);

            log.info("Обновлена категория с id: {}", savedCategory.getId() );

            return CategoryMapper.toCategoryDto(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Название категории должно быть уникальным");
        }
    }

    @Override
    public void deleteCategory(long categoryId) {
        log.info("Удалить категорию с id: " + categoryId);

        Category category = validCategory(categoryId);

        try {
            repository.deleteById(categoryId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Нельзя удалять категорию: " + category.getName());
        }
    }

    @Override
    public CategoryDto getCategoryById(long categoryId) {
        log.info("Получить категорию по id: {}", categoryId);

        Category category = validCategory(categoryId);

        log.info("Получена категория с id: {}", categoryId);

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("Получить все категории");

        validPagination(from, size);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Category> categories = repository.findAll(pageRequest).getContent();

        if (categories.isEmpty()) {
            log.info("Список категорий пуст");

            return Collections.emptyList();
        }

        log.info("Получены все категории");

        return categories.stream()
            .map(CategoryMapper::toCategoryDto)
            .collect(Collectors.toList());
    }

    private Category validCategory(long categoryId) {
        return repository.findById(categoryId).orElseThrow(
            () -> new NotFoundException("Не найдена категория с id: " + categoryId)
        );
    }

    private void validPagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не должны быть отрицательными");
        }
    }
}
