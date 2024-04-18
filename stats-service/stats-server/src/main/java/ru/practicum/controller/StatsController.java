package ru.practicum.controller;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

@Slf4j
@RestController
@Data
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
        @RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime start,
        @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
        @RequestParam(name = "uris", defaultValue = "") List<String> uris,
        @RequestParam(name = "unique", defaultValue = "false") Boolean unique
    ) {
        log.info("Контроллер - запрос статистики");

        return service.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto save(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Контроллер - сохранить статистику");

        return service.save(endpointHitDto);
    }
}
