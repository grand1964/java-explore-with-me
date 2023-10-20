package ru.practicum.ewm.controller.private_api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@AllArgsConstructor
@Validated
public class PrivateRequestController {
    private RequestService requestService;

    ////////////////////////////// Запросы заявок ////////////////////////////

    //запрос заявок пользователя на участие в чужих событиях
    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable long userId) {
        log.debug("Запрошено получение списка своих событий инициатором");
        return requestService.getUserRequests(userId);
    }

    ///////////////////////////// Создание заявок ////////////////////////////

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable long userId,
                                                                 @RequestParam Long eventId) {
        log.info("Запрошено создание новой заявки на участие в событии " + eventId);
        /*return ResponseEntity
                .status(201)
                .body(requestService.createRequest(userId, eventId));*/
        ParticipationRequestDto dto = requestService.createRequest(userId, eventId);
        ResponseEntity<ParticipationRequestDto> resp = ResponseEntity
                .status(201)
                .body(dto);
        return resp;
    }

    //////////////////////////// Обновление событий //////////////////////////

    @PatchMapping(path = "/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable long userId,
                                          @PathVariable long requestId) {
        log.info("Запрошена отмена пользователем своей заявки " + requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
