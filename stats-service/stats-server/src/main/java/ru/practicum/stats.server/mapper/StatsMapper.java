package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.Utils;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.model.StatsEntity;

import java.time.LocalDateTime;


@Mapper(componentModel = "spring")
public interface StatsMapper {


    @Mapping(expression = "java(getDate(endpointHit.getTimestamp()))", target = "statsTime")
    StatsEntity toEntity(EndpointHit endpointHit);

    EndpointHit toEndpointHit(StatsEntity entity);

    ViewStats toViewStats(StatsEntity entity);

    default LocalDateTime getDate(String date) {
        return LocalDateTime.parse(date, Utils.DATE_FORMATTER);
    }
}
