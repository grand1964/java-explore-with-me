package ru.practicum.ewm.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatInDto;
import ru.practicum.ewm.StatOutDto;
import ru.practicum.ewm.service.StatService;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor(onConstructor_ = @Autowired)
public class StatController {
    private final StatService service;

    ///////////////////////////// Получение данных ///////////////////////////

    @GetMapping(value = "/stats")
    public ResponseEntity<List<StatOutDto>> getStat(@NotBlank @RequestParam String start,
                                                    @NotBlank @RequestParam String end,
                                                    @RequestParam String[] uris,
                                                    @RequestParam(defaultValue = "false") Boolean unique
    ) {
        log.info("Запрошена статистика с {} по {} ", start, end);
        return ResponseEntity.ok(service.getStat(start, end, uris, unique));
    }

    ///////////////////////////// Добавление данных /////////////////////////

    //создание нового запроса
    @PostMapping(value = "/hit")
    public ResponseEntity<Void> putStat(@RequestBody StatInDto statInDto) {
        log.info("Информация сохранена ");
        service.putHit(statInDto);
        return new ResponseEntity<>(HttpStatus.valueOf(201));
    }
}
