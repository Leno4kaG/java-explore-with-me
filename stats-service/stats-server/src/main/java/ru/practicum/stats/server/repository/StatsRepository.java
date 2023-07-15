package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.model.StatsEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<StatsEntity, Long> {

    @Query("SELECT new ru.practicum.stats.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM StatsEntity AS s " +
            "WHERE s.statsTime BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> findAllByStatsAndUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM StatsEntity AS s " +
            "WHERE s.statsTime BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> findAllByStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.dto.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM StatsEntity AS s " +
            "WHERE s.statsTime BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> findAllByStatsUniqueAndUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.ViewStats(s.app, s.uri, COUNT(distinct s.ip)) " +
            "FROM StatsEntity AS s " +
            "WHERE s.statsTime BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> findAllByStatsUnique(LocalDateTime start, LocalDateTime end);

}
