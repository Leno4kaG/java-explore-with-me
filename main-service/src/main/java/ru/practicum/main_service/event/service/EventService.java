package ru.practicum.main_service.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.category.domain.repository.CategoryRepository;
import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.event.domain.model.Location;
import ru.practicum.main_service.event.domain.repository.EventRepository;
import ru.practicum.main_service.event.domain.repository.LocationRepository;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.dto.LocationDto;
import ru.practicum.main_service.event.dto.NewEventDto;
import ru.practicum.main_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.main_service.event.dto.UpdateEventUserRequest;
import ru.practicum.main_service.event.enums.EventSortType;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.mapper.EventMapper;
import ru.practicum.main_service.event.mapper.LocationMapper;
import ru.practicum.main_service.exception.ForbiddenException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.domain.model.User;
import ru.practicum.main_service.user.domain.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsService statsService;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;

    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Получение всех событий для администратора с параметрами users = {}, states = {}, categoriesId = {}, " +
                        "rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        List<Event> events = eventRepository.findAllForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);

        return toEventsFullDto(events);
    }


    @Transactional
    public EventFullDto editEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Обновления события с id {} по запросу администратора с параметрами {}", eventId, updateEventAdminRequest);

        checkNewEventDate(updateEventAdminRequest.getEventDate(), LocalDateTime.now().plusHours(1));

        Event event = getEventById(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена.")));
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventAdminRequest.getLocation()));
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            checkIsNewLimitNotLessOld(updateEventAdminRequest.getParticipantLimit(),
                    statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L));

            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ForbiddenException(String.format("Field: stateAction. Error: опубликовать можно только " +
                        "события, находящиеся в ожидании публикации. Текущий статус: %s", event.getState()));
            }

            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.REJECTED);
                    break;
            }
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        return toEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEventsByPrivate(Long userId, Pageable pageable) {
        log.info("Получение событий пользователя с id {} и пагинацией {}", userId, pageable);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден."));

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        return toEventsShortDto(events);
    }

    @Transactional
    public EventFullDto createEventByPrivate(Long userId, NewEventDto newEventDto) {
        log.info("Создание нового события пользователем с id {} и параметрами {}", userId, newEventDto);

        checkNewEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        User eventUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден."));
        Category eventCategory = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория не найдена."));
        Location eventLocation = getOrSaveLocation(newEventDto.getLocation());

        Event newEvent = eventMapper.toEvent(newEventDto, eventUser, eventCategory, eventLocation, LocalDateTime.now(),
                EventState.PENDING);

        return toEventFullDto(eventRepository.save(newEvent));
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByPrivate(Long userId, Long eventId) {
        log.info("Получение события с id {}, созданного пользователем с id {}", eventId, userId);

        userRepository.findById(userId);

        Event event = getEventByIdAndInitiatorId(eventId, userId);

        return toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventFullDto editEventByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Обновление события с id {} по запросу пользователя с id {} с новыми параметрами {}",
                eventId, userId, updateEventUserRequest);

        checkNewEventDate(updateEventUserRequest.getEventDate(), LocalDateTime.now().plusHours(2));

        userRepository.findById(userId);

        Event event = getEventByIdAndInitiatorId(eventId, userId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Изменять можно только неопубликованные или отмененные события.");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена.")));
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventUserRequest.getLocation()));
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        return toEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByPublic(
            String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, EventSortType sort, Integer from, Integer size, HttpServletRequest request) {
        log.info("Получение всех событий для публичного запроса с параметрами text = {}, categoriesId = {}, paid = {}, rangeStart = {}, " +
                        "rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        List<Event> events = eventRepository.findAllForPublic(text, categories,
                paid, rangeStart, rangeEnd, from, size);

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));

        List<EventShortDto> eventsShortDto = toEventsShortDto(events);

        if (onlyAvailable) {
            eventsShortDto = eventsShortDto.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > eventShort.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }

        if (needSort(sort, EventSortType.VIEWS)) {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getViews));
        } else if (needSort(sort, EventSortType.EVENT_DATE)) {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        statsService.saveHit(request);

        return eventsShortDto;
    }


    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        log.info("Получение события с id {} на публичный запрос", eventId);

        Event event = getEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с таким id не опубликовано.");
        }

        statsService.saveHit(request);

        return toEventFullDto(event);
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long eventId) {
        log.info("Вывод события с id {}", eventId);

        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с таким id не найдено."));
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByIds(List<Long> eventsId) {
        log.info("Вывод списка событий с ids {}", eventsId);

        if (eventsId.isEmpty()) {
            return new ArrayList<>();
        }

        return eventRepository.findAllByIdIn(eventsId);
    }

    public List<EventShortDto> toEventsShortDto(List<Event> events) {
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream()
                .map((event) -> eventMapper.toEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> toEventsFullDto(List<Event> events) {
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream()
                .map((event) -> eventMapper.toEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private EventFullDto toEventFullDto(Event event) {
        return toEventsFullDto(List.of(event)).get(0);
    }

    private Event getEventByIdAndInitiatorId(Long eventId, Long userId) {
        log.info("Получение события с id {}", eventId);

        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("События с таким id не найдено."));
    }

    private Location getOrSaveLocation(LocationDto locationDto) {
        Location newLocation = locationMapper.toLocation(locationDto);
        return locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon())
                .orElseGet(() -> locationRepository.save(newLocation));
    }

    private Boolean needSort(EventSortType sort, EventSortType typeToCompare) {
        return sort != null && sort.equals(typeToCompare);
    }

    private void checkStartIsBeforeEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ForbiddenException(String.format("Field: eventDate. Error: некорректные параметры временного " +
                    "интервала. Value: rangeStart = %s, rangeEnd = %s", rangeStart, rangeEnd));
        }
    }

    private void checkNewEventDate(LocalDateTime newEventDate, LocalDateTime minTimeBeforeEventStart) {
        if (newEventDate != null && newEventDate.isBefore(minTimeBeforeEventStart)) {
            throw new ForbiddenException(String.format("Field: eventDate. Error: остается слишком мало времени для " +
                    "подготовки. Value: %s", newEventDate));
        }
    }

    private void checkIsNewLimitNotLessOld(Integer newLimit, Long eventParticipantLimit) {
        if (newLimit != 0 && eventParticipantLimit != 0 && (newLimit < eventParticipantLimit)) {
            throw new ForbiddenException(String.format("Field: stateAction. Error: Новый лимит участников должен " +
                    "быть не меньше количества уже одобренных заявок: %s", eventParticipantLimit));
        }
    }
}
