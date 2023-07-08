package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.mapper.StatsMapper;
import ru.practicum.stats.server.model.StatsEntity;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Transactional
    public List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        log.info("Start {} end {} uris {}", start, end, uris);
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.findAllByStatsUnique(start, end);

            } else {
                return statsRepository.findAllByStats(start, end);
            }
        } else {
            if (unique) {
                return statsRepository.findAllByStatsUniqueAndUri(start, end, uris);

            } else {

                return statsRepository.findAllByStatsAndUri(start, end, uris);


            }
        }

    }

    @Transactional
    public void save(EndpointHit request) {
        StatsEntity stats = statsRepository.save(statsMapper.toEntity(request));
        log.info("Stats {}", stats);
    }

}
