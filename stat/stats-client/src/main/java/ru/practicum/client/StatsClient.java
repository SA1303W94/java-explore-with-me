package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate restTemplate;
    private final String serverUrl;


    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    public void create(HitDto hitDto) {
        HttpEntity<HitDto> requestEntity = new HttpEntity<>(hitDto);
        restTemplate.exchange(serverUrl + "/hit", HttpMethod.POST, requestEntity, Object.class);
    }

    public ResponseEntity<StatDto[]> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters;
        String path = null;
        if (!uris.isEmpty()) {
            parameters = Map.of(
                    "start", start,
                    "end", end,
                    "uris", String.join(",", uris),
                    "unique", unique
            );
            path = serverUrl + "/stats/?start={start}&end={end}&uris={uris}&unique={unique}";
        } else {
            parameters = Map.of(
                    "start", start,
                    "end", end,
                    "unique", unique
            );
            path = serverUrl + "/stats/?start={start}&end={end}&unique={unique}";
        }
        ResponseEntity<StatDto[]> serverResponse = restTemplate.getForEntity(path, StatDto[].class, parameters);
        if (serverResponse.getStatusCode().is2xxSuccessful()) {
            log.info("! serverResponse = {}", (Object) serverResponse.getBody());
            return serverResponse;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(serverResponse.getStatusCode());
        if (serverResponse.hasBody()) {
            return responseBuilder.body(serverResponse.getBody());
        }
        return responseBuilder.build();
    }
}