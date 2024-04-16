package ru.practicum.compilation.controller;

import java.util.List;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> getAllCompilation(
        @RequestParam(value = "pinned", required = false) Boolean pinned,
        @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
        @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return service.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compilationId}")
    public CompilationDto getCompilationById(@PathVariable Long compilationId) {
        return service.getCompilationById(compilationId);
    }
}
