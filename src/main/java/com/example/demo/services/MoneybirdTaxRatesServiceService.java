package com.example.demo.services;

import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.services.interfaces.IMoneybirdTaxRatesService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class MoneybirdTaxRatesServiceService implements IMoneybirdTaxRatesService {
    private WebClient webClientWithBaseUrl;

    public MoneybirdTaxRatesServiceService(WebClient webClientWithBaseUrl) {
        this.webClientWithBaseUrl = webClientWithBaseUrl;
    }

    @Override
    public Flux<MoneybirdTaxRate> getAllTaxRates() {
        return webClientWithBaseUrl.get()
                .uri("/tax_rates")
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToFlux(MoneybirdTaxRate.class);
                    else return response.createError().flux().cast(MoneybirdTaxRate.class);
                });
    }
}
