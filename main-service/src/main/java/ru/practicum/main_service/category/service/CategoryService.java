package ru.practicum.main_service.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.category.domain.repository.CategoryRepository;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории {}", newCategoryDto);

        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.newCategoryDtoToCategory(newCategoryDto)));
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Pageable pageable) {
        log.info("Получение всех категорий {}", pageable);

        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getById(Long catId) {
        log.info("Получение категории по id {}", catId);

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с таким id не найдена."));

        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto editCategory(Long catId, CategoryDto categoryDto) {
        log.info("Обновление категории с id {} новыми параметрами {}", catId, categoryDto);

        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с таким id не найдена."));

        categoryDto.setId(catId);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.categoryDtoToCategory(categoryDto)));
    }

    @Transactional
    public void deleteById(Long catId) {
        log.info("Удаление категории с id {}", catId);

        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с таким id не найдена."));

        categoryRepository.deleteById(catId);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long catId) {
        log.info("Вывод категории с id {}", catId);

        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с таким id не найдена."));
    }
}
