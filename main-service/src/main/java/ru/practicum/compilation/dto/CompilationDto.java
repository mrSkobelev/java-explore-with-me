package ru.practicum.compilation.dto;

import java.util.Set;
import lombok.Data;
import ru.practicum.event.dto.EventShortDto;

@Data
public class CompilationDto {
    private Long id;
    private String title;
    private Set<EventShortDto> events;
    private Boolean pinned;
}
