package ru.practicum.category.controller;

import java.util.List;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService service;

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable Long categoryId) {
        return service.getCategoryById(categoryId);
    }

    @GetMapping
    public List<CategoryDto> getAllCategories(
        @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
        @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return service.getAllCategories(from, size);
    }
}
