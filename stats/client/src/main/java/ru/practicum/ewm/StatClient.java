package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Component
public class StatClient {
    private final RestTemplate rest;

    @Autowired
    //public StatClient(@Value("http://192.168.50.4:8080") String serverUrl, RestTemplateBuilder builder) {
    public StatClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
    //TODO Исправить !!!!
    //public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<List<StatOutDto>> get(
            @NotBlank String start, @NotBlank String end, String[] uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        ParameterizedTypeReference<List<StatOutDto>> getRef = new ParameterizedTypeReference<>() {
        };

        HttpEntity<Object> requestEntity = new HttpEntity<>(null, defaultHeaders());
        try {
            return rest.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                    HttpMethod.GET, requestEntity, getRef, parameters);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Внутренняя ошибка сервера");
        }
    }

    public ResponseEntity<Void> post(StatInDto body) {
        HttpEntity<StatInDto> requestEntity = new HttpEntity<>(body, defaultHeaders());
        try {
            return rest.exchange("/hit", HttpMethod.POST, requestEntity, Void.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Внутренняя ошибка сервера статистики");
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
