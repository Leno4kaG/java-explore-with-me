package ru.practicum.statsservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsservice.service.StatsService;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsdto.EndpointHit;


import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/stats")
    public Set<ViewStats> getAllStats(@RequestParam(name = "start", defaultValue = "0") String start,
                                      @RequestParam(name = "end", defaultValue = "0") String end,
                                      @RequestParam(name = "uris") List<String> uris,
                                      @RequestParam(name = "unique", defaultValue = "false") boolean unique){
        return statsService.getAllStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public ResponseEntity<HttpStatus> createStats(@RequestBody EndpointHit request){
    statsService.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
