package ru.practicum.stats.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.Utils;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.model.StatsEntity;
import ru.practicum.stats.server.repository.StatsRepository;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {
    @Mock
    private StatsRepository statsRepository;
    @Mock
    private StatsMapperImpl statsMapper;

    @InjectMocks
    private StatsService statsService;

    @Captor
    private ArgumentCaptor<StatsEntity> statsArgumentCaptor;

    private final EndpointHit endpointHit = EndpointHit.builder()
            .app("APP")
            .uri("/test/uri/1")
            .ip("127.0.0.1")
            .timestamp("2023-07-06 12:00:23")
            .build();

    private final ViewStats viewStats1 = new ViewStats("APP 1", "/test/uri/1", 1L);

    private final ViewStats viewStats2 = new ViewStats("APP 2", "/test/uri/2", 2L);

    private final LocalDateTime start = LocalDateTime.of(2023, 7, 1, 0, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2023, 12, 1, 0, 0, 0);
    private final List<String> uris = List.of("/test/uri/1", "/test/uri/2");

    @Test
    public void getAllUniqueWhenUriIsNull() {
        when(statsRepository.findAllByStatsUnique(start, end)).thenReturn(List.of(viewStats1, viewStats2));

        List<ViewStats> stats = statsService.getAllStats(start, end, null, true);

        verify(statsRepository, times(1)).findAllByStatsUnique(start, end);

        assertEquals(2, stats.size());
        assertEquals(viewStats1, stats.get(0));
        assertEquals(viewStats2, stats.get(1));

    }

    @Test
    public void getAllNotUniqueWhenUriIsNull() {
        when(statsRepository.findAllByStats(start, end)).thenReturn(List.of(viewStats1, viewStats2));

        List<ViewStats> stats = statsService.getAllStats(start, end, null, false);

        verify(statsRepository, times(1)).findAllByStats(start, end);

        assertEquals(2, stats.size());
        assertEquals(viewStats1, stats.get(0));
        assertEquals(viewStats2, stats.get(1));
    }

    @Test
    public void getAllUniqueByUri() {
        when(statsRepository.findAllByStatsUniqueAndUri(start, end, uris)).thenReturn(List.of(viewStats1, viewStats2));

        List<ViewStats> stats = statsService.getAllStats(start, end, uris, true);

        verify(statsRepository, times(1)).findAllByStatsUniqueAndUri(start, end, uris);

        assertEquals(2, stats.size());
        assertEquals(viewStats1, stats.get(0));
        assertEquals(viewStats2, stats.get(1));
    }

    @Test
    public void getAllNoUniqueByUri() {
        when(statsRepository.findAllByStatsAndUri(start, end, uris)).thenReturn(List.of(viewStats1, viewStats2));

        List<ViewStats> stats = statsService.getAllStats(start, end, uris, false);

        verify(statsRepository, times(1)).findAllByStatsAndUri(start, end, uris);

        assertEquals(2, stats.size());
        assertEquals(viewStats1, stats.get(0));
        assertEquals(viewStats2, stats.get(1));
    }

    @Test
    public void save() {
        when(statsMapper.toEntity(any())).thenCallRealMethod();

        statsService.save(endpointHit);

        verify(statsMapper, times(1)).toEntity(any());
        verify(statsRepository, times(1)).save(statsArgumentCaptor.capture());

        StatsEntity savedEntity = statsArgumentCaptor.getValue();

        assertEquals(endpointHit.getApp(), savedEntity.getApp());
        assertEquals(endpointHit.getUri(), savedEntity.getUri());
        assertEquals(endpointHit.getIp(), savedEntity.getIp());
        assertEquals(LocalDateTime.parse(endpointHit.getTimestamp(), Utils.DATE_FORMATTER), savedEntity.getStatsTime());
    }

}
