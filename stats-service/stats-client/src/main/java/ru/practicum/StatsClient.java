package ru.practicum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

@Service
@Getter
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(@Value("${stats-server.url}") String url, RestTemplateBuilder builder) {
        super(builder
            .uriTemplateHandler(new DefaultUriBuilderFactory(url))
            .requestFactory(HttpComponentsClientHttpRequestFactory::new)
            .build());
    }

    public ResponseEntity<Object> hit(EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<List<ViewStatsDto>> stats(
        LocalDateTime start,
        LocalDateTime end,
        List<String> uris,
        Boolean unique) {
        Map<String, Object> parameters = Map.of(
            "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "uris",String.join(",", uris),
            "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}
