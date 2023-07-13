package ru.practicum.main_service.event.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.category.domain.repository.CategoryRepository;
import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.event.domain.model.Location;
import ru.practicum.main_service.event.domain.model.Request;
import ru.practicum.main_service.event.domain.repository.EventRepository;
import ru.practicum.main_service.event.domain.repository.LocationRepository;
import ru.practicum.main_service.event.domain.repository.RequestRepository;
import ru.practicum.main_service.event.dto.RequestStats;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.enums.RequestStatus;
import ru.practicum.main_service.user.domain.model.User;
import ru.practicum.main_service.user.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestRepositoryTest {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    private final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("test1@yandex.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("test2@yandex.ru")
            .build();
    private final User user3 = User.builder()
            .id(3L)
            .name("user3")
            .email("test3@yandex.ru")
            .build();
    private final User user4 = User.builder()
            .id(4L)
            .name("user4")
            .email("test4@yandex.ru")
            .build();
    private final Category category = Category.builder()
            .id(1L)
            .name("category")
            .build();
    private final Location location = Location.builder()
            .id(1L)
            .lat(34.5624F)
            .lon(-8.0077F)
            .build();
    private final Event event1 = Event.builder()
            .id(1L)
            .title("title 1")
            .annotation("annotation 1")
            .description("description 1")
            .eventDate(LocalDateTime.now().plusDays(2))
            .category(category)
            .location(location)
            .paid(false)
            .participantLimit(0)
            .requestModeration(false)
            .initiator(user1)
            .state(EventState.PENDING)
            .createdOn(LocalDateTime.now().minusHours(7))
            .publishedOn(LocalDateTime.now().minusHours(4))
            .build();
    private final Event event2 = Event.builder()
            .id(2L)
            .title("title 2")
            .annotation("annotation 2")
            .description("description 2")
            .eventDate(LocalDateTime.now().plusDays(4))
            .category(category)
            .location(location)
            .paid(true)
            .participantLimit(50)
            .requestModeration(true)
            .initiator(user1)
            .state(EventState.PUBLISHED)
            .createdOn(LocalDateTime.now().minusDays(1))
            .publishedOn(LocalDateTime.now().minusHours(2))
            .build();
    private final Event event3 = Event.builder()
            .id(3L)
            .title("title 3")
            .annotation("annotation 3")
            .description("description 3")
            .eventDate(LocalDateTime.now().plusDays(8))
            .category(category)
            .location(location)
            .paid(true)
            .participantLimit(500)
            .requestModeration(true)
            .initiator(user1)
            .state(EventState.PUBLISHED)
            .createdOn(LocalDateTime.now().minusDays(5))
            .publishedOn(LocalDateTime.now().minusHours(2))
            .build();
    private final Request request1 = Request.builder()
            .id(1L)
            .event(event1)
            .requester(user2)
            .created(event1.getPublishedOn().plusHours(1))
            .status(RequestStatus.CONFIRMED)
            .build();
    private final Request request2 = Request.builder()
            .id(2L)
            .event(event1)
            .requester(user3)
            .created(event1.getPublishedOn().plusHours(2))
            .status(RequestStatus.CONFIRMED)
            .build();
    private final Request request3 = Request.builder()
            .id(3L)
            .event(event1)
            .requester(user4)
            .created(event1.getPublishedOn().plusHours(3))
            .status(RequestStatus.PENDING)
            .build();
    private final Request request4 = Request.builder()
            .id(4L)
            .event(event2)
            .requester(user2)
            .created(event2.getPublishedOn().plusHours(1))
            .status(RequestStatus.CANCELED)
            .build();
    private final Request request5 = Request.builder()
            .id(5L)
            .event(event2)
            .requester(user4)
            .created(event2.getPublishedOn().plusHours(3))
            .status(RequestStatus.CONFIRMED)
            .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        categoryRepository.save(category);
        locationRepository.save(location);
        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);
        requestRepository.save(request1);
        requestRepository.save(request2);
        requestRepository.save(request3);
        requestRepository.save(request4);
        requestRepository.save(request5);
    }

    @Nested
    class FindAllByRequesterId {
        @Test
        public void findAllByRequesterId() {
            List<Request> requestsFromRepository = requestRepository.findAllByRequesterId(user2.getId());

            assertEquals(2, requestsFromRepository.size());

            Request requestFromRepository1 = requestsFromRepository.get(0);
            Request requestFromRepository2 = requestsFromRepository.get(1);

            assertEquals(request1.getId(), requestFromRepository1.getId());
            assertEquals(request4.getId(), requestFromRepository2.getId());
        }

        @Test
        public void findAllByRequesterIdTest() {
            List<Request> requestsFromRepository = requestRepository.findAllByRequesterId(user3.getId());

            assertEquals(1, requestsFromRepository.size());

            Request requestFromRepository1 = requestsFromRepository.get(0);

            assertEquals(request2.getId(), requestFromRepository1.getId());
        }

        @Test
        public void findAllByRequesterIdWhenEmpty() {
            List<Request> requestsFromRepository = requestRepository.findAllByRequesterId(user1.getId());

            assertTrue(requestsFromRepository.isEmpty());
        }
    }

    @Nested
    class FindByEventIdAndRequesterId {
        @Test
        public void findByEventIdAndRequesterId() {
            Optional<Request> optionalRequest = requestRepository.findByEventIdAndRequesterId(event2.getId(), user2.getId());

            assertTrue(optionalRequest.isPresent());

            Request requestFromRepository = optionalRequest.get();

            assertEquals(request4.getId(), requestFromRepository.getId());
        }

        @Test
        public void FindByEventIdAndRequesterIdWhenEmpty() {
            Optional<Request> optionalRequest = requestRepository.findByEventIdAndRequesterId(event2.getId(), user3.getId());

            assertTrue(optionalRequest.isEmpty());
        }
    }

    @Nested
    class FindAllByEventIdAndStatus {
        @Test
        public void findAllByEventIdAndStatus() {
            List<Request> requestsFromRepository = requestRepository.findAllByEventIdAndStatus(event1.getId(), RequestStatus.CONFIRMED);

            assertEquals(2, requestsFromRepository.size());

            Request requestFromRepository1 = requestsFromRepository.get(0);
            Request requestFromRepository2 = requestsFromRepository.get(1);

            assertEquals(request1.getId(), requestFromRepository1.getId());
            assertEquals(request2.getId(), requestFromRepository2.getId());
        }

        @Test
        public void findAllByEventIdAndStatusTest() {
            List<Request> requestsFromRepository = requestRepository.findAllByEventIdAndStatus(event2.getId(), RequestStatus.CONFIRMED);

            assertEquals(1, requestsFromRepository.size());

            Request requestFromRepository1 = requestsFromRepository.get(0);

            assertEquals(request5.getId(), requestFromRepository1.getId());
        }

        @Test
        public void findAllByEventIdAndStatusWhenEmpty() {
            List<Request> requestsFromRepository = requestRepository.findAllByEventIdAndStatus(event2.getId(), RequestStatus.REJECTED);

            assertTrue(requestsFromRepository.isEmpty());
        }
    }

    @Nested
    class FindAllByEventId {
        @Test
        public void findAllByEventId() {
            List<Request> requestsFromRepository = requestRepository.findAllByEventId(event1.getId());

            assertEquals(3, requestsFromRepository.size());

            Request requestFromRepository1 = requestsFromRepository.get(0);
            Request requestFromRepository2 = requestsFromRepository.get(1);
            Request requestFromRepository3 = requestsFromRepository.get(2);

            assertEquals(request1.getId(), requestFromRepository1.getId());
            assertEquals(request2.getId(), requestFromRepository2.getId());
            assertEquals(request3.getId(), requestFromRepository3.getId());
        }

        @Test
        public void findAllByEventIdTest() {
            List<Request> requestsFromRepository = requestRepository.findAllByEventId(event2.getId());

            assertEquals(2, requestsFromRepository.size());

            Request requestFromRepository1 = requestsFromRepository.get(0);
            Request requestFromRepository2 = requestsFromRepository.get(1);

            assertEquals(request4.getId(), requestFromRepository1.getId());
            assertEquals(request5.getId(), requestFromRepository2.getId());
        }

        @Test
        public void findAllByEventIdWhenEmpty() {
            List<Request> requestsFromRepository = requestRepository.findAllByEventId(event3.getId());

            assertTrue(requestsFromRepository.isEmpty());
        }
    }

    @Nested
    class FindAllByIdIn {
        @Test
        public void findAllByIdIn() {
            List<Request> requestsFromRepository = requestRepository.findAllByIdIn(List.of(1L, 3L));

            assertEquals(2, requestsFromRepository.size());

            Request requestFromRepository1 = requestsFromRepository.get(0);
            Request requestFromRepository2 = requestsFromRepository.get(1);

            assertEquals(request1.getId(), requestFromRepository1.getId());
            assertEquals(request3.getId(), requestFromRepository2.getId());
        }

        @Test
        public void findAllByIdInWhenNotFound() {
            List<Request> requestsFromRepository = requestRepository.findAllByIdIn(List.of(99L));

            assertTrue(requestsFromRepository.isEmpty());
        }

        @Test
        public void findAllByIdInWhenIdsIsEmpty() {
            List<Request> requestsFromRepository = requestRepository.findAllByIdIn(List.of());

            assertTrue(requestsFromRepository.isEmpty());
        }
    }

    @Nested
    class GetConfirmedRequests {
        @Test
        public void getConfirmedRequests() {
            List<RequestStats> requestStatsFromRepository = requestRepository.getConfirmedRequests(
                    List.of(event1.getId(), event2.getId()));

            assertEquals(2, requestStatsFromRepository.size());

            RequestStats requestStats1 = requestStatsFromRepository.get(0);
            RequestStats requestStats2 = requestStatsFromRepository.get(1);

            assertEquals(event1.getId(), requestStats1.getEventId());
            assertEquals(2L, requestStats1.getConfirmedRequests());

            assertEquals(event2.getId(), requestStats2.getEventId());
            assertEquals(1L, requestStats2.getConfirmedRequests());
        }

        @Test
        public void getConfirmedRequestsTest() {
            List<RequestStats> requestStatsFromRepository = requestRepository.getConfirmedRequests(List.of(event2.getId()));

            assertEquals(1L, requestStatsFromRepository.size());

            RequestStats requestStats1 = requestStatsFromRepository.get(0);

            assertEquals(event2.getId(), requestStats1.getEventId());
            assertEquals(1L, requestStats1.getConfirmedRequests());
        }

        @Test
        public void getConfirmedRequestsWhenEmpty() {
            List<RequestStats> requestStatsFromRepository = requestRepository.getConfirmedRequests(List.of());

            assertTrue(requestStatsFromRepository.isEmpty());
        }
    }
}
