package ru.practicum;

import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.ViewStatsDto;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate restTemplate) {
        this.rest = restTemplate;
    }

    protected ResponseEntity<List<ViewStatsDto>> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequestGet(HttpMethod.GET, path, parameters);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(
        HttpMethod method,
        String path,
        @Nullable Map<String, Object> parameters,
        @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private ResponseEntity<List<ViewStatsDto>> makeAndSendRequestGet(
        HttpMethod method,
        String path,
        @Nullable Map<String, Object> parameters) {
        HttpEntity<List<ViewStatsDto>> requestEntity = new HttpEntity<>(null, defaultHeaders());

        ResponseEntity<List<ViewStatsDto>> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<>() {
                }, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<>() {
                });
            }
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException(e.getMessage());
        }
        return prepareGatewayResponseGet(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private static ResponseEntity<List<ViewStatsDto>> prepareGatewayResponseGet(
        ResponseEntity<List<ViewStatsDto>> response
    ) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
