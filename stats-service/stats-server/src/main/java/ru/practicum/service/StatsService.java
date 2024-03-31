package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

public interface StatsService {

    EndpointHitDto save(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
