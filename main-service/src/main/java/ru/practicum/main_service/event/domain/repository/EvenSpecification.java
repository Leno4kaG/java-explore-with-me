package ru.practicum.main_service.event.domain.repository;

import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EvenSpecification {

    List<Event> findAllForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<Event> findAllForPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Integer from, Integer size);
}
