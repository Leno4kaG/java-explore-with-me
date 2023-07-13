package ru.practicum.main_service.category;

import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.mapper.CategoryMapper;

public class CategoryMapperImpl implements CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        } else {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setName(category.getName());
            return categoryDto;
        }
    }

    public Category categoryDtoToCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        } else {
            Category category = new Category();
            category.setId(categoryDto.getId());
            category.setName(categoryDto.getName());
            return category;
        }
    }

    public Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            return null;
        } else {
            Category category = new Category();
            category.setName(newCategoryDto.getName());
            return category;
        }
    }
}
