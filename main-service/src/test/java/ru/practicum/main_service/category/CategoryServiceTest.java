package ru.practicum.main_service.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.category.domain.repository.CategoryRepository;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.service.CategoryService;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapperImpl categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private final Pageable pageable = PageRequest.of(0 / 10, 10);

    @Captor
    private ArgumentCaptor<Category> categoryArgumentCaptor;

    private final NewCategoryDto newCategoryDto = NewCategoryDto.builder()
            .name("category 1")
            .build();
    private final Category category1 = Category.builder()
            .id(1L)
            .name(newCategoryDto.getName())
            .build();
    private final Category category2 = Category.builder()
            .id(2L)
            .name("category 2")
            .build();
    private CategoryDto categoryDto2;

    @BeforeEach
    public void beforeEach() {
        categoryDto2 = CategoryDto.builder()
                .id(2L)
                .name("category 2")
                .build();
    }

    @Test
    public void addCategory() {
        when(categoryMapper.newCategoryDtoToCategory(any())).thenCallRealMethod();
        when(categoryMapper.toCategoryDto(any())).thenCallRealMethod();
        when(categoryRepository.save(any())).thenReturn(category1);


        CategoryDto savedCategoryDto = categoryService.addCategory(newCategoryDto);

        assertEquals(category1.getId(), savedCategoryDto.getId());
        assertEquals(category1.getName(), savedCategoryDto.getName());

        verify(categoryMapper, times(1)).newCategoryDtoToCategory(any());
        verify(categoryMapper, times(1)).toCategoryDto(any());
        verify(categoryRepository, times(1)).save(categoryArgumentCaptor.capture());

        Category savedCategory = categoryArgumentCaptor.getValue();

        assertNull(savedCategory.getId());
        assertEquals(newCategoryDto.getName(), savedCategory.getName());
    }

    @Test
    public void getAll() {
        when(categoryMapper.toCategoryDto(any())).thenCallRealMethod();
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(category1, category2)));

        List<CategoryDto> categoriesDtoFromService = categoryService.getAll(pageable);

        assertEquals(2, categoriesDtoFromService.size());
        assertEquals(category1.getId(), categoriesDtoFromService.get(0).getId());
        assertEquals(category1.getName(), categoriesDtoFromService.get(0).getName());
        assertEquals(category2.getId(), categoriesDtoFromService.get(1).getId());
        assertEquals(category2.getName(), categoriesDtoFromService.get(1).getName());

        verify(categoryMapper, times(2)).toCategoryDto(any());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getAllWhenCategoryOne() {
        when(categoryMapper.toCategoryDto(any())).thenCallRealMethod();
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(category2)));

        List<CategoryDto> categoriesDtoFromService = categoryService.getAll(pageable);

        assertEquals(1, categoriesDtoFromService.size());
        assertEquals(category2.getId(), categoriesDtoFromService.get(0).getId());
        assertEquals(category2.getName(), categoriesDtoFromService.get(0).getName());

        verify(categoryMapper, times(1)).toCategoryDto(any());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getAllWhenCategoryEmpty() {
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<CategoryDto> categoriesDtoFromService = categoryService.getAll(pageable);

        assertTrue(categoriesDtoFromService.isEmpty());

        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getById() {
        when(categoryMapper.toCategoryDto(any())).thenCallRealMethod();
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category1));

        CategoryDto categoryDto = categoryService.getById(category1.getId());

        assertEquals(category1.getId(), categoryDto.getId());
        assertEquals(category1.getName(), categoryDto.getName());

        verify(categoryMapper, times(1)).toCategoryDto(any());
        verify(categoryRepository, times(1)).findById(any());
    }

    @Test
    public void getByIdWhenIdNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryService.getById(99L));
        assertEquals("Категория с таким id не найдена.", exception.getMessage());

        verify(categoryRepository, times(1)).findById(any());
    }

    @Test
    public void editCategory() {
        when(categoryMapper.toCategoryDto(any())).thenCallRealMethod();
        when(categoryMapper.categoryDtoToCategory(any())).thenCallRealMethod();
        when(categoryRepository.findById(category1.getId())).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any()))
                .thenReturn(Category.builder()
                        .id(category1.getId())
                        .name(categoryDto2.getName())
                        .build());

        CategoryDto categoryDto = categoryService.editCategory(category1.getId(), categoryDto2);

        assertEquals(category1.getId(), categoryDto.getId());
        assertEquals(categoryDto2.getName(), categoryDto.getName());

        verify(categoryMapper, times(1)).toCategoryDto(any());
        verify(categoryMapper, times(1)).categoryDtoToCategory(any());
        verify(categoryRepository, times(1)).findById(any());
        verify(categoryRepository, times(1)).save(categoryArgumentCaptor.capture());

        Category savedCategory = categoryArgumentCaptor.getValue();

        assertEquals(category1.getId(), savedCategory.getId());
        assertEquals(categoryDto2.getName(), savedCategory.getName());
    }

    @Test
    public void editCategoryWhenIdNotFound() {
        when(categoryRepository.findById(category1.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryService.editCategory(category1.getId(), categoryDto2));
        assertEquals("Категория с таким id не найдена.", exception.getMessage());

        verify(categoryRepository, times(1)).findById(any());
        verify(categoryRepository, never()).save(any());
    }

}
