package ru.practicum.compilation.service;

import java.util.List;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest request);

    void deleteCompilation(Long compilationId);

    CompilationDto getCompilationById(Long compilationId);

    List<CompilationDto> getAllCompilations(Boolean pined, Integer from, Integer size);
}
