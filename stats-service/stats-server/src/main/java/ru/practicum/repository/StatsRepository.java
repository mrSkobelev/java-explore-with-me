package ru.practicum.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {
    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where timestamp between ?1 and ?2 and e.uri in ?3 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStatsDto> findByUniqueWithUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where timestamp between ?1 and ?2 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStatsDto> findByUniqueWithoutUri(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(e.ip)) " +
        "from EndpointHit as e " +
        "where timestamp between ?1 and ?2 and e.uri in ?3 " +
        "group by e.app, e.uri " +
        "order by count(e.ip) desc")
    List<ViewStatsDto> findByNotUniqueWithUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(e.ip)) " +
        "from EndpointHit as e " +
        "where timestamp between ?1 and ?2 " +
        "group by e.app, e.uri " +
        "order by count(e.ip) desc")
    List<ViewStatsDto> findByNotUniqueWithoutUri(LocalDateTime start, LocalDateTime end);
}
