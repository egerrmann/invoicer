package com.example.demo.services.interfaces;

import com.example.demo.models.moneybird.MoneybirdTaxRate;
import reactor.core.publisher.Flux;

public interface IMoneybirdTaxRatesService {
    Flux<MoneybirdTaxRate> getAllTaxRates();

    Flux<MoneybirdTaxRate> getAllTaxRates(String countryISO);
}
