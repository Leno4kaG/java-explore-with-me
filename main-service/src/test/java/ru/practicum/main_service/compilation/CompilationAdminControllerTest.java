package ru.practicum.main_service.compilation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main_service.compilation.controller.CompilationAdminController;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main_service.compilation.service.CompilationService;
import ru.practicum.main_service.event.dto.EventShortDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompilationAdminController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationAdminControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private CompilationService compilationService;

    private final EventShortDto eventShortDto = EventShortDto.builder()
            .id(2L)
            .build();

    private final CompilationDto compilationDto = CompilationDto.builder()
            .id(1L)
            .title("test title")
            .pinned(false)
            .events(List.of(eventShortDto))
            .build();

    private NewCompilationDto newCompilationDto;
    private UpdateCompilationRequest updateCompilation;

    @BeforeEach
    public void beforeEach() {
        newCompilationDto = NewCompilationDto.builder()
                .title("title 1")
                .pinned(false)
                .events(List.of(1L))
                .build();

        updateCompilation = UpdateCompilationRequest.builder()
                .title("title update")
                .pinned(false)
                .events(List.of(1L))
                .build();
    }

    @Test
    public void addCompilation() throws Exception {
        when(compilationService.addCompilation(any())).thenReturn(compilationDto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(compilationDto)));

        verify(compilationService, times(1)).addCompilation(any());
    }

    @Test
    public void addCompilationWhenPinnedIsNull() throws Exception {
        newCompilationDto.setPinned(null);

        when(compilationService.addCompilation(any())).thenReturn(compilationDto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(compilationDto)));

        verify(compilationService, times(1)).addCompilation(any());
    }

    @Test
    public void addCompilationWhenEventsIsNull() throws Exception {
        newCompilationDto.setEvents(null);

        when(compilationService.addCompilation(any())).thenReturn(compilationDto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(compilationDto)));

        verify(compilationService, times(1)).addCompilation(any());
    }

    @Test
    public void addCompilationWhenTitleIsNullOrEmpty() throws Exception {
        newCompilationDto.setTitle(null);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(compilationService, never()).addCompilation(any());

        newCompilationDto.setTitle("");

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(compilationService, never()).addCompilation(any());

        newCompilationDto.setTitle("    ");

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(compilationService, never()).addCompilation(any());
    }


    @Test
    public void updateCompilation() throws Exception {
        when(compilationService.update(any(), any())).thenReturn(compilationDto);

        mvc.perform(patch("/admin/compilations/1")
                        .content(mapper.writeValueAsString(updateCompilation))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(compilationDto)));

        verify(compilationService, times(1)).update(any(), any());
    }

    @Test
    public void updateCompilationWhenPinnedIsNull() throws Exception {
        updateCompilation.setPinned(null);

        when(compilationService.update(any(), any())).thenReturn(compilationDto);

        mvc.perform(patch("/admin/compilations/1")
                        .content(mapper.writeValueAsString(updateCompilation))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(compilationDto)));

        verify(compilationService, times(1)).update(any(), any());
    }

    @Test
    public void updateCompilationWhenEventsIsNull() throws Exception {
        updateCompilation.setEvents(null);

        when(compilationService.update(any(), any())).thenReturn(compilationDto);

        mvc.perform(patch("/admin/compilations/1")
                        .content(mapper.writeValueAsString(updateCompilation))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(compilationDto)));

        verify(compilationService, times(1)).update(any(), any());
    }

    @Test
    public void updateCompilationWhenTitleIsNullOrEmpty() throws Exception {
        updateCompilation.setTitle(null);

        when(compilationService.update(anyLong(), any(UpdateCompilationRequest.class))).thenReturn(compilationDto);

        mvc.perform(patch("/admin/compilations/1")
                        .content(mapper.writeValueAsString(updateCompilation))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(compilationDto)));

        verify(compilationService, times(1)).update(any(), any());
    }


    @Test
    public void deleteCompilation() throws Exception {
        mvc.perform(delete("/admin/compilations/1")
                        .content(mapper.writeValueAsString(updateCompilation))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(compilationService, times(1)).deleteById(any());
    }

}
