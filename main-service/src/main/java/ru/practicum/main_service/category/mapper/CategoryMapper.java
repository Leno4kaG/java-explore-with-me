package ru.practicum.main_service.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    Category categoryDtoToCategory(CategoryDto categoryDto);

    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);
}
