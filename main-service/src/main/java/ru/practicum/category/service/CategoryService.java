package ru.practicum.category.service;

import java.util.List;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(long categoryId, CategoryDto categoryDto);

    void deleteCategory(long categoryId);

    CategoryDto getCategoryById(long categoryId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);
}
