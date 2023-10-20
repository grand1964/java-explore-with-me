package ru.practicum.ewm.controller.admin_api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@AllArgsConstructor
@Validated
public class AdminCompilationController {
    private CompilationService compilationService;

    //создание новой подборки
    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto dto) {
        log.info("Запрошено создание новой подборки");
        return ResponseEntity
                .status(201)
                .body(compilationService.createCompilation(dto));
    }

    //удаление подборки
    @DeleteMapping(value = "/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable long compId) {
        log.info("Запрошено удаление подборки с идентификатором " + compId);
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    //обновление подборки
    @PatchMapping(value = "/{compId}")
    public CompilationDto updateCompilation(@PathVariable long compId,
                                            @Valid @RequestBody UpdateCompilationRequest dto) {
        log.info("Запрошено обновление подборки с идентификатором " + compId);
        return compilationService.updateCompilation(compId, dto);
    }
}
