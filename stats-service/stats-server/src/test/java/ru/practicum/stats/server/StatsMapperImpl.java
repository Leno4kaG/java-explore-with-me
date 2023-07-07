package ru.practicum.stats.server;

import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.Utils;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.mapper.StatsMapper;
import ru.practicum.stats.server.model.StatsEntity;

import java.time.LocalDateTime;

public class StatsMapperImpl implements StatsMapper {


    @Override
    public StatsEntity toEntity(EndpointHit endpointHit) {
        if (endpointHit == null) {
            return null;
        }

        StatsEntity statsEntity = new StatsEntity();

        statsEntity.setApp(endpointHit.getApp());
        statsEntity.setUri(endpointHit.getUri());
        statsEntity.setIp(endpointHit.getIp());

        statsEntity.setStatsTime(LocalDateTime.parse(endpointHit.getTimestamp(), Utils.DATE_FORMATTER));

        return statsEntity;
    }

    @Override
    public EndpointHit toEndpointHit(StatsEntity entity) {
        if (entity == null) {
            return null;
        }

        EndpointHit.EndpointHitBuilder endpointHit = EndpointHit.builder();

        endpointHit.app(entity.getApp());
        endpointHit.uri(entity.getUri());
        endpointHit.ip(entity.getIp());

        return endpointHit.build();
    }

    @Override
    public ViewStats toViewStats(StatsEntity entity) {
        if (entity == null) {
            return null;
        }

        ViewStats viewStats = new ViewStats();

        viewStats.setApp(entity.getApp());
        viewStats.setUri(entity.getUri());

        return viewStats;
    }

}