package com.example.demo.services.interfaces;

import com.example.demo.models.moneybird.MoneybirdAdministration;
import reactor.core.publisher.Flux;

public interface IMoneybirdAdministrationService {
    Flux<MoneybirdAdministration> getAdministrations(String accessToken);
}
