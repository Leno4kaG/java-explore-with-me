package ru.practicum.statsservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.model.StatsEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    @Mapping(expression = "java(getDate(endpointHit.getTimestamp()))", target = "statsTime")
    StatsEntity toEntity(EndpointHit endpointHit);

    EndpointHit toEndpointHit(StatsEntity entity);

    default Set<ViewStats> toViewList(List<StatsEntity> statsEntities){
        if(statsEntities.isEmpty()){
            return Collections.emptySet();
        }
        Set<ViewStats> viewStatsList = new HashSet<>();
        ViewStats viewStats = new ViewStats();
        int hits = 1;
        if(statsEntities.size()==1){
            viewStats.setApp(statsEntities.get(0).getApp());
            viewStats.setUri(statsEntities.get(0).getUri());
            viewStats.setHits(hits);
            viewStatsList.add(viewStats);
            return viewStatsList;
        }
        for(int i=0; i<statsEntities.size(); i++) {
            if (statsEntities.get(i).getUri().equals(statsEntities.get(i + 1).getUri())) {
                viewStats.setApp(statsEntities.get(i).getApp());
                viewStats.setUri(statsEntities.get(i).getUri());
                hits++;
            } else {
                hits = 1;
                viewStats.setApp(statsEntities.get(i).getApp());
                viewStats.setUri(statsEntities.get(i).getUri());
            }
            viewStats.setHits(hits);
            viewStatsList.add(viewStats);
        }
            return viewStatsList;
        }

        default LocalDateTime getDate(String date){
        return LocalDateTime.parse(date, formatter);
        }
}
