package ru.practicum.main_service.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main_service.category.controller.CategoryAdminController;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.service.CategoryService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryAdminController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryAdminControllerTest {

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private CategoryService categoryService;

    private final CategoryDto categoryDto1 = CategoryDto.builder()
            .id(1L)
            .name("category 1")
            .build();
    private CategoryDto categoryDto2;
    private NewCategoryDto newCategoryDto;

    @BeforeEach
    public void beforeEach() {
        newCategoryDto = NewCategoryDto.builder()
                .name(categoryDto1.getName())
                .build();

        categoryDto2 = CategoryDto.builder()
                .id(2L)
                .name("category 2")
                .build();
    }

    @Test
    public void addCategory() throws Exception {
        when(categoryService.addCategory(any())).thenReturn(categoryDto1);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(categoryDto1)));

        verify(categoryService, times(1)).addCategory(any());
    }

    @Test
    public void addCategoryWhenNameIsNullOrEmpty() throws Exception {
        newCategoryDto.setName(null);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).addCategory(any());

        newCategoryDto.setName("");

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).addCategory(any());

        newCategoryDto.setName(" ");

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).addCategory(any());
    }

    @Test
    public void editCategory() throws Exception {
        when(categoryService.editCategory(ArgumentMatchers.eq(1L), ArgumentMatchers.any())).thenReturn(categoryDto2);

        mvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(categoryDto2)));

        verify(categoryService, times(1)).editCategory(any(), any());
    }

    @Test
    public void editCategoryWhenNameIsNullOrEmpty() throws Exception {
        categoryDto2.setName(null);

        mvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).editCategory(any(), any());

        categoryDto2.setName("");

        mvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).editCategory(any(), any());

        categoryDto2.setName(" ");

        mvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).editCategory(any(), any());
    }

    @Test
    public void deleteTest() throws Exception {
        mvc.perform(delete("/admin/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteById(ArgumentMatchers.any());
    }

}
