package ru.practicum.compilation.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
      log.info("Создать подборку");

        List<Event> events;

        if (newCompilationDto.getEvents() == null) {
            events = Collections.emptyList();
        } else {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }

        Set<Event> eventSet = new HashSet<>();
        eventSet.addAll(events);

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, eventSet);
        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("Создана подборка с id: {}", compilation.getId());

        return CompilationMapper.toCompilationDto(savedCompilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest request) {
        log.info("Обновить подборку с id: {}", compilationId);

        Compilation compilation = validCompilation(compilationId);

        Boolean pinned = request.getPinned();
        if (pinned != null) {
            compilation.setPinned(pinned);
        }

        String title = request.getTitle();
        if (title != null && !title.isBlank()) {
            compilation.setTitle(title);
        }

        Set<Long> eventIds = request.getEvents();
        if (eventIds != null) {
            Set<Event> events = new HashSet<>();
               events.addAll(eventRepository.findAllById(eventIds));
            compilation.setEvents(events);
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);

        log.info("Обновлена подборка с id: {}", compilationId);

        return CompilationMapper.toCompilationDto(updatedCompilation);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        log.info("Удалить подборку с id: {}", compilationId);

        validCompilation(compilationId);
        compilationRepository.deleteById(compilationId);

        log.info("Удалена подборка с id: {}", compilationId);
    }

    @Override
    public CompilationDto getCompilationById(Long compilationId) {
        log.info("Получить подборку с id: {}", compilationId);

        Compilation compilation = validCompilation(compilationId);

        log.info("Получена подборка с id: {}", compilationId);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Получить все подборки событий");

        validPagination(from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findCompilationByPinnedEquals(pinned, pageRequest).getContent();
        }

        if (compilations.isEmpty()) {
            log.info("Получен пустой список подборок событий");

            return Collections.emptyList();
        }

        log.info("Получен список подборок событий");

        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    private Compilation validCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(
            () -> new NotFoundException("Не найдена подборка с id: " + compilationId)
        );
    }

    private void validPagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не должны быть отрицательными");
        }
    }
}
