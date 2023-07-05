package ru.practicum.statsdto;

import lombok.Data;

@Data
public class EndpointHit {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
