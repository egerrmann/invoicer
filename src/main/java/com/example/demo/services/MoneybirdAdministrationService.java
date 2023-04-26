package com.example.demo.services;

import com.example.demo.models.moneybird.MoneybirdAdministration;
import com.example.demo.services.interfaces.IMoneybirdAdministrationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class MoneybirdAdministrationService implements IMoneybirdAdministrationService {
    @Override
    public Flux<MoneybirdAdministration> getAdministrations(String accessToken) {
        return WebClient.create()
                .get()
                .uri("https://moneybird.com/api/v2/administrations.json")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                // TODO: add access token
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToFlux(MoneybirdAdministration.class);
                    else
                        return response.createError()
                                .flux()
                                .cast(MoneybirdAdministration.class);
                });
    }
}
