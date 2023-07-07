package ru.practicum.stats.server.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.Utils;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping(Utils.STATS)
    public List<ViewStats> getAllStats(@RequestParam(name = "start") @DateTimeFormat(pattern = Utils.DATE_FORMAT) LocalDateTime start,
                                      @RequestParam(name = "end") @DateTimeFormat(pattern = Utils.DATE_FORMAT) LocalDateTime end,
                                      @RequestParam(name = "uris", required = false) List<String> uris,
                                      @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return statsService.getAllStats(start, end, uris, unique);
    }

    @PostMapping(Utils.HIT)
    @ResponseStatus(HttpStatus.CREATED)
    public void createStats(@Valid @RequestBody EndpointHit request) {
        statsService.save(request);
    }

}
