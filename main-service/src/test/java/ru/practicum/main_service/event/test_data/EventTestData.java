package ru.practicum.main_service.event.test_data;

import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.event.domain.model.Request;
import ru.practicum.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.enums.RequestStatus;
import ru.practicum.main_service.user.domain.model.User;

import java.time.LocalDateTime;

public class EventTestData {

    public static final User user1 = User.builder()
            .id(1L)
            .name("test user1")
            .email("test1@yandex.ru")
            .build();
    public static final User user2 = User.builder()
            .id(2L)
            .name("test user2")
            .email("test2@yandex.ru")
            .build();
    public static final User user3 = User.builder()
            .id(3L)
            .name("test user3")
            .email("test3@yandex.ru")
            .build();
    public static final Event event1 = Event.builder()
            .id(1L)
            .eventDate(LocalDateTime.now().plusDays(2))
            .participantLimit(0)
            .requestModeration(false)
            .initiator(user1)
            .state(EventState.PENDING)
            .createdOn(LocalDateTime.now().minusHours(7))
            .publishedOn(LocalDateTime.now().minusHours(4))
            .build();
    public static final Event event2 = Event.builder()
            .id(2L)
            .eventDate(LocalDateTime.now().plusDays(4))
            .participantLimit(2)
            .requestModeration(true)
            .initiator(user1)
            .state(EventState.PUBLISHED)
            .createdOn(LocalDateTime.now().minusDays(1))
            .publishedOn(LocalDateTime.now().minusHours(2))
            .build();
    public static final Event event3 = Event.builder()
            .id(3L)
            .eventDate(LocalDateTime.now().plusDays(8))
            .participantLimit(500)
            .requestModeration(false)
            .initiator(user1)
            .state(EventState.PUBLISHED)
            .createdOn(LocalDateTime.now().minusDays(5))
            .publishedOn(LocalDateTime.now().minusHours(2))
            .build();
    public static final Request request1 = Request.builder()
            .id(1L)
            .event(event1)
            .requester(user2)
            .created(event1.getPublishedOn().plusHours(1))
            .status(RequestStatus.CONFIRMED)
            .build();
    public static final Request request2 = Request.builder()
            .id(2L)
            .event(event3)
            .requester(user2)
            .created(event3.getPublishedOn().plusHours(2))
            .status(RequestStatus.CONFIRMED)
            .build();
    public static final Request request3 = Request.builder()
            .id(3L)
            .event(event1)
            .requester(user3)
            .created(event1.getPublishedOn().plusHours(3))
            .status(RequestStatus.PENDING)
            .build();
    public static final Request request4 = Request.builder()
            .id(4L)
            .event(event2)
            .requester(user2)
            .created(event2.getPublishedOn().plusHours(1))
            .status(RequestStatus.PENDING)
            .build();
    public static final ParticipationRequestDto participationRequestDto1 = ParticipationRequestDto.builder()
            .id(request1.getId())
            .event(request1.getEvent().getId())
            .requester(request1.getRequester().getId())
            .created(request1.getCreated())
            .status(request1.getStatus())
            .build();
    public static final ParticipationRequestDto participationRequestDto2 = ParticipationRequestDto.builder()
            .id(request2.getId())
            .event(request2.getEvent().getId())
            .requester(request2.getRequester().getId())
            .created(request2.getCreated())
            .status(request2.getStatus())
            .build();
    public static final ParticipationRequestDto participationRequestDto4 = ParticipationRequestDto.builder()
            .id(request4.getId())
            .event(request4.getEvent().getId())
            .requester(request4.getRequester().getId())
            .created(request4.getCreated())
            .status(request4.getStatus())
            .build();
}
