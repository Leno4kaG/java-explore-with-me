package ru.practicum.main_service.event.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.main_service.event.domain.model.Location;
import ru.practicum.main_service.event.domain.repository.LocationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LocationRepositoryTest {
    private final LocationRepository locationRepository;

    private final Location location1 = Location.builder()
            .id(1L)
            .lon(-16.789F)
            .lat(8.7890F)
            .build();
    private final Location location2 = Location.builder()
            .id(2L)
            .lon(7.6789F)
            .lat(-6.7766F)
            .build();

    @BeforeEach
    public void beforeEach() {
        locationRepository.save(location1);
        locationRepository.save(location2);
    }

    @Nested
    class FindByLatAndLon {
        @Test
        public void findByLatAndLon() {
            Optional<Location> optionalLocation = locationRepository.findByLatAndLon(location2.getLat(),
                    location2.getLon());

            assertTrue(optionalLocation.isPresent());

            Location locationFromRepository = optionalLocation.get();

            assertEquals(location2, locationFromRepository);
        }

        @Test
        public void findByLatAndLonWhenEmpty() {
            Optional<Location> optionalLocation = locationRepository.findByLatAndLon(0F, 0F);

            assertTrue(optionalLocation.isEmpty());
        }
    }
}
