package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitMapper;
import ru.practicum.repository.StatsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public EndpointHitDto save(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        EndpointHit savedEndpointHit = repository.save(endpointHit);
        log.info("Сохранена запись статистики {}", savedEndpointHit);
        return EndpointHitMapper.toEndpointHitDto(savedEndpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris,
        Boolean unique) {
        List<ViewStatsDto> viewStatsDtos;
        validSearchEventDate(start, end);

        if (unique) {
            if (uris.isEmpty()) {
                viewStatsDtos = repository.findByUniqueWithoutUri(start, end);
            } else {
                viewStatsDtos = repository.findByUniqueWithUri(start, end, uris);
            }
        } else {
            if (uris.isEmpty()) {
                viewStatsDtos = repository.findByNotUniqueWithoutUri(start, end);
            } else {
                viewStatsDtos = repository.findByNotUniqueWithUri(start, end, uris);
            }
        }
        log.info("Получен список статистики {}", viewStatsDtos);

        return viewStatsDtos;
    }

    private void validSearchEventDate(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть позже даты конца");
        }
    }
}
