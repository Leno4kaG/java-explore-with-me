package ru.practicum.statsservice.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "stats")
public class StatsEntity {
    @Id
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime statsTime;
}
