package ru.practicum.statsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.statsservice.model.StatsEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<StatsEntity, Long> {
    List<StatsEntity> findAllByStatsTimeBetweenAndUriIn(@Param("startDate") LocalDateTime start,
                                                        @Param("endDate") LocalDateTime end,
                                                        List<String> uris);

}
