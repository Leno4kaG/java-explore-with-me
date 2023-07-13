package ru.practicum.main_service.compilation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.compilation.domain.model.Compilation;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.user.domain.model.User;

import java.util.List;

public class EventTestData {

    public final static Pageable pageable = PageRequest.of(0 / 10, 10);
    public final static User user1 = User.builder()
            .id(1L)
            .build();
    public final static User user2 = User.builder()
            .id(2L)
            .build();
    public final static Category category1 = Category.builder()
            .id(1L)
            .name("category 1")
            .build();
    public final static Category category2 = Category.builder()
            .id(2L)
            .name("category 2")
            .build();
    public final static NewCompilationDto newCompilationDto1 = NewCompilationDto.builder()
            .title("title 1")
            .pinned(false)
            .events(List.of(1L, 2L))
            .build();
    public final static NewCompilationDto newCompilationDto2 = NewCompilationDto.builder()
            .title("title 2")
            .pinned(true)
            .events(List.of())
            .build();
    public final static UpdateCompilationRequest updateCompilationRequest1 = UpdateCompilationRequest.builder()
            .title("title update")
            .pinned(true)
            .events(List.of(1L))
            .build();
    public final static Event event1 = Event.builder()
            .id(1L)
            .category(category1)
            .initiator(user1)
            .build();
    public final static Event event2 = Event.builder()
            .id(2L)
            .category(category2)
            .initiator(user2)
            .build();
    public final static EventShortDto eventShortDto1 = EventShortDto.builder()
            .id(event1.getId())
            .views(0L)
            .confirmedRequests(10L)
            .build();
    public final static EventShortDto eventShortDto2 = EventShortDto.builder()
            .id(event2.getId())
            .views(10L)
            .confirmedRequests(0L)
            .build();
    public final static Compilation compilation1 = Compilation.builder()
            .id(1L)
            .title(newCompilationDto1.getTitle())
            .pinned(newCompilationDto1.getPinned())
            .events(List.of(event1, event2))
            .build();
    public final static Compilation compilation2 = Compilation.builder()
            .id(2L)
            .title(newCompilationDto2.getTitle())
            .pinned(newCompilationDto2.getPinned())
            .events(List.of())
            .build();
    public final static Compilation updatedCompilation1 = Compilation.builder()
            .id(compilation1.getId())
            .title(updateCompilationRequest1.getTitle())
            .pinned(updateCompilationRequest1.getPinned())
            .events(List.of(event1))
            .build();
    public final static CompilationDto compilationDto1 = CompilationDto.builder()
            .id(compilation1.getId())
            .title(compilation1.getTitle())
            .pinned(compilation1.getPinned())
            .events(List.of(eventShortDto1, eventShortDto2))
            .build();
    public final static CompilationDto compilationDto2 = CompilationDto.builder()
            .id(compilation2.getId())
            .title(compilation2.getTitle())
            .pinned(compilation2.getPinned())
            .events(List.of())
            .build();
    public final static CompilationDto updatedCompilationDto1 = CompilationDto.builder()
            .id(updatedCompilation1.getId())
            .title(updatedCompilation1.getTitle())
            .pinned(updatedCompilation1.getPinned())
            .events(List.of(eventShortDto1))
            .build();
}
