package ru.practicum.main_service.event.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.category.domain.repository.CategoryRepository;
import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.event.domain.model.Location;
import ru.practicum.main_service.event.domain.repository.EventRepository;
import ru.practicum.main_service.event.domain.repository.LocationRepository;
import ru.practicum.main_service.event.enums.EventState;
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
public class EventRepositoryTest {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;

    private final Pageable pageable = PageRequest.of(0 / 10, 10);
    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("test@yandex.ru")
            .build();
    private final Category category = Category.builder()
            .id(1L)
            .name("category")
            .build();
    private final Location location = Location.builder()
            .id(1L)
            .lat(19.5677F)
            .lon(-8.0765F)
            .build();
    private final Event event1 = Event.builder()
            .id(1L)
            .title("test title 1")
            .annotation("test annotation 1")
            .description("test description 1")
            .eventDate(LocalDateTime.now().plusDays(2))
            .category(category)
            .location(location)
            .paid(false)
            .participantLimit(0)
            .requestModeration(false)
            .initiator(user)
            .state(EventState.PENDING)
            .createdOn(LocalDateTime.now())
            .publishedOn(null)
            .build();
    private final Event event2 = Event.builder()
            .id(2L)
            .title("test title 2")
            .annotation("test annotation 2")
            .description("test description 2")
            .eventDate(LocalDateTime.now().plusDays(6))
            .category(category)
            .location(location)
            .paid(true)
            .participantLimit(50)
            .requestModeration(true)
            .initiator(user)
            .state(EventState.PUBLISHED)
            .createdOn(LocalDateTime.now().minusDays(1))
            .publishedOn(LocalDateTime.now().minusHours(3))
            .build();
    private final Event event3 = Event.builder()
            .id(3L)
            .title("test title 3")
            .annotation("test annotation 3")
            .description("test description 3")
            .eventDate(LocalDateTime.now().plusDays(4))
            .category(category)
            .location(location)
            .paid(true)
            .participantLimit(0)
            .requestModeration(true)
            .initiator(user)
            .state(EventState.PUBLISHED)
            .createdOn(LocalDateTime.now().minusDays(2))
            .publishedOn(LocalDateTime.now().minusHours(6))
            .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.save(user);
        categoryRepository.save(category);
        locationRepository.save(location);
        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);
    }

    @Nested
    class FindAllByInitiatorId {
        @Test
        public void findAllByInitiatorId() {
            List<Event> eventsFromRepository = eventRepository.findAllByInitiatorId(user.getId(), pageable);

            assertEquals(3, eventsFromRepository.size());

            Event eventFromRepository1 = eventsFromRepository.get(0);
            Event eventFromRepository2 = eventsFromRepository.get(1);
            Event eventFromRepository3 = eventsFromRepository.get(2);

            assertEquals(event1.getId(), eventFromRepository1.getId());
            assertEquals(event2.getId(), eventFromRepository2.getId());
            assertEquals(event3.getId(), eventFromRepository3.getId());
        }

        @Test
        public void findAllWhenEmpty() {
            List<Event> eventsFromRepository = eventRepository.findAllByInitiatorId(99L, pageable);

            assertTrue(eventsFromRepository.isEmpty());
        }
    }

    @Nested
    class FindByIdAndInitiatorId {
        @Test
        public void findByIdAndInitiatorId() {
            Optional<Event> optionalEvent = eventRepository.findByIdAndInitiatorId(event2.getId(), user.getId());

            assertTrue(optionalEvent.isPresent());

            Event eventFromRepository = optionalEvent.get();

            assertEquals(event2.getId(), eventFromRepository.getId());
        }

        @Test
        public void findByIdAndInitiatorIdWhenEmpty() {
            Optional<Event> optionalEvent = eventRepository.findByIdAndInitiatorId(99L, user.getId());

            assertTrue(optionalEvent.isEmpty());
        }
    }

    @Nested
    class FindAllByIdIn {
        @Test
        public void findAllByIdIn() {
            List<Event> eventsFromRepository = eventRepository.findAllByIdIn(List.of(event1.getId(), event2.getId()));

            assertEquals(2, eventsFromRepository.size());

            Event eventFromRepository1 = eventsFromRepository.get(0);
            Event eventFromRepository2 = eventsFromRepository.get(1);

            assertEquals(event1.getId(), eventFromRepository1.getId());
            assertEquals(event2.getId(), eventFromRepository2.getId());
        }

        @Test
        public void findAllByIdInEmptyWhenNotFound() {
            List<Event> eventsFromRepository = eventRepository.findAllByIdIn(List.of(99L));

            assertTrue(eventsFromRepository.isEmpty());
        }

        @Test
        public void findAllByIdInEmptyWhenIdsIsEmpty() {
            List<Event> eventsFromRepository = eventRepository.findAllByIdIn(List.of());

            assertTrue(eventsFromRepository.isEmpty());
        }
    }

    @Nested
    class FindAllEventsByAdmin {
        @Test
        public void findAllEventsByAdmin() {
            List<Event> eventsFromRepository = eventRepository.findAllForAdmin(List.of(user.getId()),
                    List.of(EventState.values()), List.of(category.getId()), LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(5),0, 10);

            assertEquals(2, eventsFromRepository.size());

            Event eventFromRepository1 = eventsFromRepository.get(0);
            Event eventFromRepository2 = eventsFromRepository.get(1);

            assertEquals(event1.getId(), eventFromRepository1.getId());
            assertEquals(event3.getId(), eventFromRepository2.getId());
        }

        @Test
        public void findAllEventsByAdminTest() {
            List<Event> eventsFromRepository = eventRepository.findAllForAdmin(List.of(user.getId()),
                    List.of(EventState.PUBLISHED), List.of(category.getId()), LocalDateTime.now().plusDays(5),
                    LocalDateTime.now().plusDays(6),0, 10);

            assertEquals(1, eventsFromRepository.size());

            Event eventFromRepository1 = eventsFromRepository.get(0);

            assertEquals(event2.getId(), eventFromRepository1.getId());
        }

        @Test
        public void findAllEventsByAdminReturnAll() {
            List<Event> eventsFromRepository = eventRepository.findAllForAdmin(null, null, null,
                    null, null,0,10);

            assertEquals(3, eventsFromRepository.size());

            Event eventFromRepository1 = eventsFromRepository.get(0);
            Event eventFromRepository2 = eventsFromRepository.get(1);
            Event eventFromRepository3 = eventsFromRepository.get(2);

            assertEquals(event1.getId(), eventFromRepository1.getId());
            assertEquals(event2.getId(), eventFromRepository2.getId());
            assertEquals(event3.getId(), eventFromRepository3.getId());
        }

        @Test
        public void findAllEventsByAdminWhenEmpty() {
            List<Event> eventsFromRepository = eventRepository.findAllForAdmin(List.of(45L), null, null,
                    null, null, 0,10);

            assertTrue(eventsFromRepository.isEmpty());
        }

    }

    @Nested
    class FindAllEventsByPublic {
        @Test
        public void findAllEventsByPublic() {
            List<Event> eventsFromRepository = eventRepository.findAllForPublic("TeSt", List.of(category.getId()),
                    true, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(7), 0,10);

            assertEquals(2, eventsFromRepository.size());

            Event eventFromRepository1 = eventsFromRepository.get(0);
            Event eventFromRepository2 = eventsFromRepository.get(1);

            assertEquals(event2.getId(), eventFromRepository1.getId());
            assertEquals(event3.getId(), eventFromRepository2.getId());
        }

        @Test
        public void findAllEventsByPublicTest() {
            List<Event> eventsFromRepository = eventRepository.findAllForPublic("ON 2", List.of(category.getId()),
                    true, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(7), 0,10);

            assertEquals(1, eventsFromRepository.size());

            Event eventFromRepository1 = eventsFromRepository.get(0);

            assertEquals(event2.getId(), eventFromRepository1.getId());
        }

        @Test
        public void findAllEventsByPublicAllPublished() {
            List<Event> eventsFromRepository = eventRepository.findAllForPublic(null, null, null,
                    null, null, 0,10);

            assertEquals(2, eventsFromRepository.size());

            Event eventFromRepository1 = eventsFromRepository.get(0);
            Event eventFromRepository2 = eventsFromRepository.get(1);

            assertEquals(event2.getId(), eventFromRepository1.getId());
            assertEquals(event3.getId(), eventFromRepository2.getId());
        }

        @Test
        public void findAllEventsByPublicWhenEmpty() {
            List<Event> eventsFromRepository = eventRepository.findAllForPublic("rtyuibh tyybh", List.of(category.getId()),
                    true, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(7), 0,10);

            assertTrue(eventsFromRepository.isEmpty());
        }
    }

}
