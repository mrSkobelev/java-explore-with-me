package ru.practicum.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {
    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where timestamp between :start and :end and e.uri in :uris " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStatsDto> findByUniqueWithUri(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where timestamp between :start and :end " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStatsDto> findByUniqueWithoutUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(e.ip)) " +
        "from EndpointHit as e " +
        "where timestamp between :start and :end and e.uri in :uris " +
        "group by e.app, e.uri " +
        "order by count(e.ip) desc")
    List<ViewStatsDto> findByNotUniqueWithUri(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end,
                                                @Param("uris") List<String> uris);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(e.ip)) " +
        "from EndpointHit as e " +
        "where timestamp between :start and :end " +
        "group by e.app, e.uri " +
        "order by count(e.ip) desc")
    List<ViewStatsDto> findByNotUniqueWithoutUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
