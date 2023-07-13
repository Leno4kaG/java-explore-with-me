package ru.practicum.main_service.compilation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.main_service.compilation.domain.model.Compilation;
import ru.practicum.main_service.compilation.domain.repository.CompilationRepository;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.mapper.CompilationMapperImpl;
import ru.practicum.main_service.compilation.service.CompilationService;
import ru.practicum.main_service.event.domain.repository.EventRepository;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.main_service.compilation.EventTestData.*;

@ExtendWith(MockitoExtension.class)
public class CompilationServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventService eventService;

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private CompilationMapperImpl compilationMapper;

    @InjectMocks
    private CompilationService compilationService;

    @Captor
    private ArgumentCaptor<Compilation> compilationArgumentCaptor;

    @Test
    public void addCompilation() {
        when(eventRepository.findAllByIdIn(any())).thenReturn(List.of(event1, event2));
        when(compilationMapper.newDtoToCompilation(any(), any())).thenCallRealMethod();
        when(compilationRepository.save(any())).thenReturn(compilation1);
        when(compilationRepository.findById(any())).thenReturn(Optional.of(compilation1));
        when(eventService.toEventsShortDto(List.of(event1, event2))).thenReturn(List.of(eventShortDto1, eventShortDto2));
        when(compilationMapper.toCompilationDto(any(), any())).thenCallRealMethod();

        CompilationDto savedCompilationDto = compilationService.addCompilation(newCompilationDto1);

        assertEquals(compilationDto1.getId(), savedCompilationDto.getId());
        assertEquals(compilationDto1.getTitle(), savedCompilationDto.getTitle());
        assertEquals(compilationDto1.getPinned(), savedCompilationDto.getPinned());
        assertEquals(compilationDto1.getEvents().size(), savedCompilationDto.getEvents().size());

        verify(eventRepository, times(1)).findAllByIdIn(any());
        verify(compilationMapper, times(1)).newDtoToCompilation(any(), any());
        verify(compilationRepository, times(1)).save(compilationArgumentCaptor.capture());
        verify(compilationRepository, times(1)).findById(any());
        verify(eventService, times(1)).toEventsShortDto(any());
        verify(compilationMapper, times(1)).toCompilationDto(any(), any());

        Compilation savedCompilation = compilationArgumentCaptor.getValue();

        assertNull(savedCompilation.getId());
        assertEquals(compilation1.getTitle(), savedCompilation.getTitle());
        assertEquals(compilation1.getPinned(), savedCompilation.getPinned());
        assertEquals(compilation1.getEvents().size(), savedCompilation.getEvents().size());
    }

    @Test
    public void addCompilationWithEmptyEvents() {
        when(compilationMapper.newDtoToCompilation(any(), any())).thenCallRealMethod();
        when(compilationRepository.save(any())).thenReturn(compilation2);
        when(compilationRepository.findById(any())).thenReturn(Optional.of(compilation2));
        when(eventService.toEventsShortDto(List.of())).thenReturn(List.of());
        when(compilationMapper.toCompilationDto(any(), any())).thenCallRealMethod();

        CompilationDto savedCompilationDto = compilationService.addCompilation(newCompilationDto2);

        assertEquals(compilationDto2.getId(), savedCompilationDto.getId());
        assertEquals(compilationDto2.getTitle(), savedCompilationDto.getTitle());
        assertEquals(compilationDto2.getPinned(), savedCompilationDto.getPinned());
        assertEquals(compilationDto2.getEvents().size(), savedCompilationDto.getEvents().size());

        verify(compilationMapper, times(1)).newDtoToCompilation(any(), any());
        verify(compilationRepository, times(1)).save(compilationArgumentCaptor.capture());
        verify(compilationRepository, times(1)).findById(any());
        verify(eventService, times(1)).toEventsShortDto(any());
        verify(compilationMapper, times(1)).toCompilationDto(any(), any());

        Compilation savedCompilation = compilationArgumentCaptor.getValue();

        assertNull(savedCompilation.getId());
        assertEquals(compilation2.getTitle(), savedCompilation.getTitle());
        assertEquals(compilation2.getPinned(), savedCompilation.getPinned());
        assertEquals(compilation2.getEvents().size(), savedCompilation.getEvents().size());
    }

    @Test
    public void addCompilationWhenEventNotFound() {
        when(eventRepository.findAllByIdIn(any())).thenReturn(List.of(event1));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> compilationService.addCompilation(newCompilationDto1));
        assertEquals("Часть событий не найдена.", exception.getMessage());

        verify(eventRepository, times(1)).findAllByIdIn(any());
        verify(compilationRepository, never()).save(any());
    }

    @Nested
    class UpdateCompilation {
        @Test
        public void update() {
            when(compilationRepository.findById(any())).thenReturn(Optional.of(compilation1));
            when(eventRepository.findAllByIdIn(any())).thenReturn(List.of(event1));
            when(compilationRepository.save(any())).thenReturn(updatedCompilation1);
            when(eventService.toEventsShortDto(List.of(event1))).thenReturn(List.of(eventShortDto1));
            when(compilationMapper.toCompilationDto(any(), any())).thenCallRealMethod();

            CompilationDto savedCompilationDto = compilationService.update(compilation1.getId(), updateCompilationRequest1);

            assertEquals(updatedCompilationDto1.getId(), savedCompilationDto.getId());
            assertEquals(updatedCompilationDto1.getTitle(), savedCompilationDto.getTitle());
            assertEquals(updatedCompilationDto1.getPinned(), savedCompilationDto.getPinned());
            assertEquals(updatedCompilationDto1.getEvents().size(), savedCompilationDto.getEvents().size());

            verify(compilationRepository, times(2)).findById(any());
            verify(eventRepository, times(1)).findAllByIdIn(any());
            verify(compilationRepository, times(1)).save(compilationArgumentCaptor.capture());
            verify(eventService, times(1)).toEventsShortDto(any());
            verify(compilationMapper, times(1)).toCompilationDto(any(), any());

            Compilation savedCompilation = compilationArgumentCaptor.getValue();

            assertEquals(updatedCompilation1.getId(), savedCompilation.getId());
            assertEquals(updatedCompilation1.getTitle(), savedCompilation.getTitle());
            assertEquals(updatedCompilation1.getPinned(), savedCompilation.getPinned());
            assertEquals(updatedCompilation1.getEvents().size(), savedCompilation.getEvents().size());
        }

        @Test
        public void updateErrorWhenCompilationNotFound() {
            when(compilationRepository.findById(any())).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> compilationService.update(compilation1.getId(), updateCompilationRequest1));
            assertEquals("Подборка с таким id не найдена.", exception.getMessage());

            verify(compilationRepository, times(1)).findById(any());
            verify(compilationRepository, never()).save(any());
        }

        @Test
        public void updateErrorWhenSomeEventsIdNotFound() {
            when(compilationRepository.findById(any())).thenReturn(Optional.of(compilation1));

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> compilationService.update(compilation1.getId(), updateCompilationRequest1));
            assertEquals("Часть событий не найдена.", exception.getMessage());

            verify(compilationRepository, times(1)).findById(any());
            verify(compilationRepository, never()).save(any());
        }
    }

    @Test
    public void delete() {
        when(compilationRepository.findById(any())).thenReturn(Optional.of(compilation1));

        compilationService.deleteById(compilation1.getId());

        verify(compilationRepository, times(1)).findById(any());
        verify(compilationRepository, times(1)).deleteById(any());
    }

    @Test
    public void deleteErrorWhenNotFound() {
        when(compilationRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> compilationService.deleteById(compilation1.getId()));
        assertEquals("Подборка с таким id не найдена.", exception.getMessage());

        verify(compilationRepository, times(1)).findById(any());
        verify(compilationRepository, never()).deleteById(any());
    }

    @Nested
    class Get {
        @Test
        public void getAllWhenPinnedIsNull() {
            when(compilationRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(compilation1, compilation2)));
            when(eventService.toEventsShortDto(any())).thenReturn(List.of(eventShortDto1, eventShortDto2));
            when(compilationMapper.toCompilationDto(ArgumentMatchers.eq(compilation1), ArgumentMatchers.any()))
                    .thenCallRealMethod();
            when(compilationMapper.toCompilationDto(ArgumentMatchers.eq(compilation2), ArgumentMatchers.any()))
                    .thenCallRealMethod();

            List<CompilationDto> savedCompilationsDto = compilationService.getAll(null, pageable);

            verify(compilationRepository, times(1)).findAll(pageable);
            verify(eventService, times(1)).toEventsShortDto(any());
            verify(compilationMapper, times(2)).toCompilationDto(any(), any());

            assertEquals(2, savedCompilationsDto.size());

            CompilationDto savedCompilationDto1 = savedCompilationsDto.get(0);
            CompilationDto savedCompilationDto2 = savedCompilationsDto.get(1);

            assertEquals(compilationDto1.getId(), savedCompilationDto1.getId());
            assertEquals(compilationDto1.getTitle(), savedCompilationDto1.getTitle());
            assertEquals(compilationDto1.getPinned(), savedCompilationDto1.getPinned());
            assertEquals(compilationDto1.getEvents().size(), savedCompilationDto1.getEvents().size());
            assertEquals(compilationDto2.getId(), savedCompilationDto2.getId());
            assertEquals(compilationDto2.getTitle(), savedCompilationDto2.getTitle());
            assertEquals(compilationDto2.getPinned(), savedCompilationDto2.getPinned());
            assertEquals(compilationDto2.getEvents().size(), savedCompilationDto2.getEvents().size());
        }

        @Test
        public void getAllWhenPinnedIsNotNull() {
            when(compilationRepository.findAllByPinned(compilation2.getPinned(), pageable)).thenReturn(List.of(compilation2));
            when(eventService.toEventsShortDto(any())).thenReturn(List.of());
            when(compilationMapper.toCompilationDto(ArgumentMatchers.eq(compilation2), ArgumentMatchers.any()))
                    .thenCallRealMethod();

            List<CompilationDto> savedCompilationsDto = compilationService.getAll(compilation2.getPinned(), pageable);

            verify(compilationRepository, times(1)).findAllByPinned(any(), any());
            verify(eventService, times(1)).toEventsShortDto(any());
            verify(compilationMapper, times(1)).toCompilationDto(any(), any());

            assertEquals(1, savedCompilationsDto.size());

            CompilationDto savedCompilationDto1 = savedCompilationsDto.get(0);

            assertEquals(compilationDto2.getId(), savedCompilationDto1.getId());
            assertEquals(compilationDto2.getTitle(), savedCompilationDto1.getTitle());
            assertEquals(compilationDto2.getPinned(), savedCompilationDto1.getPinned());
            assertEquals(compilationDto2.getEvents().size(), savedCompilationDto1.getEvents().size());
        }

        @Test
        public void getById() {
            when(compilationRepository.findById(compilation1.getId())).thenReturn(Optional.of(compilation1));
            when(eventService.toEventsShortDto(compilation1.getEvents())).thenReturn(List.of(eventShortDto1, eventShortDto2));
            when(compilationMapper.toCompilationDto(ArgumentMatchers.eq(compilation1), ArgumentMatchers.any()))
                    .thenCallRealMethod();

            CompilationDto savedCompilationsDto = compilationService.getById(compilation1.getId());

            assertEquals(compilationDto1.getId(), savedCompilationsDto.getId());
            assertEquals(compilationDto1.getTitle(), savedCompilationsDto.getTitle());
            assertEquals(compilationDto1.getPinned(), savedCompilationsDto.getPinned());
            assertEquals(compilationDto1.getEvents().size(), savedCompilationsDto.getEvents().size());

            verify(compilationRepository, times(1)).findById(compilation1.getId());
            verify(eventService, times(1)).toEventsShortDto(any());
            verify(compilationMapper, times(1)).toCompilationDto(any(), any());
        }

        @Test
        public void getByIdErrorWhenNotFound() {
            when(compilationRepository.findById(compilation1.getId())).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> compilationService.getById(compilation1.getId()));
            assertEquals("Подборка с таким id не найдена.", exception.getMessage());

            verify(compilationRepository, times(1)).findById(compilation1.getId());
        }
    }

}
