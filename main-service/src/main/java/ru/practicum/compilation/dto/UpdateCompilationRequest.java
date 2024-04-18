package ru.practicum.compilation.dto;

import java.util.Set;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}
