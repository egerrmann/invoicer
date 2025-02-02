package com.example.demo.services;

import com.example.demo.models.exceptions.IncorrectDataException;
import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.services.interfaces.IEtsyService;
import com.example.demo.services.interfaces.IMoneybirdTaxRatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

// TODO Reduce number of API requests
@RequiredArgsConstructor
@Service
public class MoneybirdTaxRatesService implements IMoneybirdTaxRatesService {
    private final WebClient webClientWithBaseUrl;
    private final IEtsyService etsyService;

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
                        // In MB there is only one query parameter called "filter",
                        // that contains all the filters, which can be separated with a comma
                        .queryParam("filter",
                                "country:" + countryISO + ",tax_rate_type:sales_invoice")
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

    @Override
    public Flux<MoneybirdTaxRate> getDomesticTaxRates() {
        return getAllTaxRates()
                .filter(rate -> rate.getCountry() == null
                        && !rate.getName().contains("EU"));
    }

    @Override
    public Flux<MoneybirdTaxRate> getOutsideEUTaxRates() {
        return getAllTaxRates()
                .filter(rate -> rate.getCountry() == null
                        && rate.getName().contains("EU"));
    }

    // TODO we may consider getting TaxRates from MB only once
    //  to make interactions with MB as little as possible
    // Gets the largest tax rate by country
    @Override
    public MoneybirdTaxRate getMaxCountryTax(String customerCountryIso) {
        // TODO check what to do in case the customer's country (from Etsy receipt) is not specified:
        //  1. Should we take the TaxRate from the country of the shop?
        //  2. Or should we throw a custom/NP exception?
        if (customerCountryIso == null) {
            throw new NullPointerException("Couldn't get a country from an Etsy receipt");
        }

        String shopIso = etsyService.getShopIso();
        // is the tax of the same country as MB account owner
        boolean isTaxDomestic = customerCountryIso.equals(shopIso);

        Flux<MoneybirdTaxRate> taxRates;

        if (isTaxDomestic) {
            taxRates = getDomesticTaxRates();
        }
        else if (customerCountryIso.equals("GB")
                || customerCountryIso.equals("NO")) {
            taxRates = getOutsideEUTaxRates();
        }
        else {
            // getting the taxRates for a specified country
            taxRates = getAllTaxRates(customerCountryIso);

            // if there are no any tax rates for the specified country
            if (!taxRates.toIterable().iterator().hasNext()) {
                // This 'if' is invoked when the specified country is not added to MB tax rates table.
                // Here we use "basic" tax rates for the home-country.
                // By "basic" tax rates we mean the tax rates with no specified country.
                taxRates = getDomesticTaxRates();
            }
        }

        // Getting Max TaxRate for the country where the shop
        // is located or for the customer's country
        return getMaxTaxRate(taxRates);
    }

    // gets a max TaxRate from provided 'taxRates'
    private MoneybirdTaxRate getMaxTaxRate(Flux<MoneybirdTaxRate> taxRates) {
        /*double ratePercentage = 0;
        MoneybirdTaxRate maxRate = null;

        for (MoneybirdTaxRate rate : taxRates) {
            double currentRatePerc = Double.parseDouble(rate.getPercentage());
            if (ratePercentage <= currentRatePerc) {
                ratePercentage = currentRatePerc;
                maxRate = rate;
            }
        }
        return maxRate;*/
        return taxRates.toStream()
                .max((o1, o2) -> {
                    double o1Percentage = Double.parseDouble(o1.getPercentage());
                    double o2Percentage = Double.parseDouble(o2.getPercentage());
                    return Double.compare(o1Percentage, o2Percentage);
                })
                .orElseThrow(() -> new IncorrectDataException("Couldn't find max " +
                        "tax rate on Moneybird", HttpStatus.BAD_REQUEST));

    }
}
