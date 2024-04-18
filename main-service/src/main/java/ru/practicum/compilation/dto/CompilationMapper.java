package ru.practicum.compilation.dto;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        Compilation compilation = new Compilation();

        if (newCompilationDto.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(newCompilationDto.getPinned());
        }

        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setEvents(events);

        return compilation;
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());

        Set<EventShortDto> eventShortDtoSet = compilation.getEvents().stream()
            .map(event -> EventMapper.toEventShortDto(event, 0,0))
            .collect(Collectors.toSet());
        compilationDto.setEvents(eventShortDtoSet);

        return compilationDto;
    }
}
