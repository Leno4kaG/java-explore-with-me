package ru.practicum.statsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.StatsMapper;
import ru.practicum.statsservice.model.StatsEntity;
import ru.practicum.statsservice.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public Set<ViewStats> getAllStats(String start, String end, List<String> uris, boolean unique) {
        List<StatsEntity> statsEntities;
        log.info("Start {} end {} uris {}", start, end, uris);
        if (unique) {
            statsEntities = statsRepository.findAllByStatsTimeBetweenAndUriIn(LocalDateTime
                            .parse(start, formatter), LocalDateTime.parse(end, formatter), uris)
                    .stream().filter(distinctByKey(StatsEntity::getIp)).collect(Collectors.toList());

        } else {
            statsEntities = statsRepository.findAllByStatsTimeBetweenAndUriIn(LocalDateTime
                    .parse(start, formatter), LocalDateTime.parse(end, formatter), uris);
            log.info(statsEntities.toString());

        }
        return statsMapper.toViewList(statsEntities);
    }

    public void save(EndpointHit request) {
        statsRepository.save(statsMapper.toEntity(request));
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
