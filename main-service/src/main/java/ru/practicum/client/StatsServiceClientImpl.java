package ru.practicum.client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceClientImpl implements StatsServiceClient {
    private static final String EVENT_URI = "/events/";

    @Value("${name-app}")
    private String app;

    private final StatsClient statsClient;

    @Override
    public void saveStats(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();

        endpointHitDto.setApp(app);
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setTimestamp(LocalDateTime.now());

        statsClient.hit(endpointHitDto);
    }

    @Override
    public List<ViewStatsDto> getViews(List<Event> events) {
        Event event = events.stream()
            .max(Comparator.comparing(Event::getPublishedOn))
            .orElseThrow(() -> new ConflictException("Отсутствует дата публикации"));

        LocalDateTime start = event.getPublishedOn();
        LocalDateTime end = LocalDateTime.now();

        List<String> urls = new ArrayList<>();
        for (Event e : events) {
            String url = EVENT_URI + e.getId();
            urls.add(url);
        }

        Boolean unique = true;

        return statsClient.stats(start, end, urls, unique).getBody();
    }
}
