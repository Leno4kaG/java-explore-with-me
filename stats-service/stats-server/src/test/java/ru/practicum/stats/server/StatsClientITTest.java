package ru.practicum.stats.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.Utils;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.service.StatsService;
import ru.practicum.statsclient.StatsClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {StatsClient.class, StatsServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatsClientITTest {
    private final StatsClient statsClient;
    private final StatsService statsService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final EndpointHit endpointHit1 = EndpointHit.builder()
            .app("APP")
            .uri("/test/uri/1")
            .ip("127.0.0.1")
            .timestamp("2023-07-05 10:00:00")
            .build();
    private final EndpointHit endpointHit2 = EndpointHit.builder()
            .app("APP2")
            .uri("/test/uri/2")
            .ip("127.0.0.1")
            .timestamp("2023-07-05 11:00:00")
            .build();

    @Test
    public void saveHit() {
        statsClient.saveHit(endpointHit1.getApp(), endpointHit1.getUri(), endpointHit1.getIp(),
                LocalDateTime.parse(endpointHit1.getTimestamp(), Utils.DATE_FORMATTER));

        List<ViewStats> stats = statsService.getAllStats(
                LocalDateTime.parse(endpointHit1.getTimestamp(), Utils.DATE_FORMATTER),
                LocalDateTime.parse(endpointHit2.getTimestamp(), Utils.DATE_FORMATTER),
                List.of(endpointHit1.getUri()),
                false
        );

        assertNotNull(stats);
        assertEquals(1, stats.size());
        ViewStats viewStats1 = stats.get(0);

        assertEquals(endpointHit1.getApp(), viewStats1.getApp());
        assertEquals(endpointHit1.getUri(), viewStats1.getUri());
        assertEquals(1L, viewStats1.getHits());
    }

    @Test
    public void getAllStats() {
        statsService.save(endpointHit1);
        statsService.save(endpointHit2);
        statsService.save(endpointHit2);

        ResponseEntity<Object> response = statsClient.getAllStats(
                LocalDateTime.parse(endpointHit1.getTimestamp(), Utils.DATE_FORMATTER),
                LocalDateTime.parse(endpointHit2.getTimestamp(), Utils.DATE_FORMATTER)
        );
        try {
            List<ViewStats> stats = (mapper.readValue(mapper.writeValueAsString(response.getBody()), new TypeReference<List<ViewStats>>() {
            }));

            assertNotNull(stats);
            assertEquals(2, stats.size());

            ViewStats viewStats1 = stats.get(0);
            ViewStats viewStats2 = stats.get(1);

            assertEquals(endpointHit2.getApp(), viewStats1.getApp());
            assertEquals(endpointHit2.getUri(), viewStats1.getUri());
            assertEquals(2, viewStats1.getHits());

            assertEquals(endpointHit1.getApp(), viewStats2.getApp());
            assertEquals(endpointHit1.getUri(), viewStats2.getUri());
            assertEquals(1, viewStats2.getHits());
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }
}

