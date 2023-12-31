package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.model.StatsEntity;


@Mapper(componentModel = "spring")
public interface StatsMapper {


    @Mapping(source = "timestamp", target = "statsTime")
    StatsEntity toEntity(EndpointHit endpointHit);

    EndpointHit toEndpointHit(StatsEntity entity);

    ViewStats toViewStats(StatsEntity entity);

}
