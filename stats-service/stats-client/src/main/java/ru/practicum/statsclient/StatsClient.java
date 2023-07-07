package ru.practicum.statsclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.Utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> saveHit(String appName, String uri, String ip, LocalDateTime timestamp) {
        log.info("Отправка запроса на регистрацию обращения к appName = {}, uri = {}, ip = {}, timestamp = {}",
                appName, uri, ip, timestamp);

        EndpointHit endpointHit = EndpointHit.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp.format(Utils.DATE_FORMATTER))
                .build();
        return post(Utils.HIT, endpointHit);
    }

    public ResponseEntity<Object> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getAllStats(start, end, uris, null);
    }

    public ResponseEntity<Object> getAllStats(LocalDateTime start, LocalDateTime end) {
        return getAllStats(start, end, null, null);
    }

    public ResponseEntity<Object> getAllStats(LocalDateTime start, LocalDateTime end, Boolean unique) {
        return getAllStats(start, end, null, unique);
    }

    public ResponseEntity<Object> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Отправка запроса на получение статистики по параметрам start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Недопустимый временной промежуток.");
        }

        StringBuilder uriBuilder = new StringBuilder(Utils.STATS + "?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
                "start", start.format(Utils.DATE_FORMATTER),
                "end", end.format(Utils.DATE_FORMATTER)
        );

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }
        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }
        log.info("Uri {} map {}", uriBuilder, parameters);

        return get(uriBuilder.toString(), parameters);
    }
}
