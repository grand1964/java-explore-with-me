package ru.practicum.ewm.controller.public_api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@AllArgsConstructor
@Validated
public class PublicCompilationController {
    private CompilationService compilationService;

    //запрос подборки
    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("Запрошен список подборок");
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return compilationService.getCompilations(pinned, pageable);
    }

    //запрос информации о подборке
    @GetMapping(value = "/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.debug("Запрошена информация о подборке с идентификатором " + compId);
        return compilationService.getCompilationById(compId);
    }
}
