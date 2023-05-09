package com.example.demo.services;

import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.services.interfaces.IMoneybirdTaxRatesService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class MoneybirdTaxRatesService implements IMoneybirdTaxRatesService {
    private final WebClient webClientWithBaseUrl;

    public MoneybirdTaxRatesService(WebClient webClientWithBaseUrl) {
        this.webClientWithBaseUrl = webClientWithBaseUrl;
    }

    @Override
    public Flux<MoneybirdTaxRate> getAllTaxRates() {
        return webClientWithBaseUrl.get()
                .uri("/tax_rates")
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToFlux(MoneybirdTaxRate.class);
                    else
                        return response.createError()
                                .flux()
                                .cast(MoneybirdTaxRate.class);
                });
    }

    // This method returns tax rates filtered by country
    @Override
    public Flux<MoneybirdTaxRate> getAllTaxRates(String countryISO) {
        return webClientWithBaseUrl.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/tax_rates")
                        // In MB it there is only one query parameter called "filter", that contains all the filters, which can be separated with a coma
                    .queryParam("filter", "country:" + countryISO)
                    .build()
                )
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToFlux(MoneybirdTaxRate.class);
                    else
                        return response.createError()
                                .flux()
                                .cast(MoneybirdTaxRate.class);
                });
    }
}
