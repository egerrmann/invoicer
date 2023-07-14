package com.example.demo.services;

import com.example.demo.models.moneybird.MoneybirdLedgerAccount;
import com.example.demo.services.interfaces.IMoneybirdLedgerAccountService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MoneybirdLedgerAccountService implements IMoneybirdLedgerAccountService {
    // TODO Check if it works with final keyword after assigning a new webclient at a runtime
    private final WebClient webClientWithBaseUrl;
    private final LedgerWrapper wrappedLedger;

    @Override
    public Flux<MoneybirdLedgerAccount> getAllLedgers() {
        return webClientWithBaseUrl.get()
                .uri("/ledger_accounts.json")
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToFlux(MoneybirdLedgerAccount.class);
                    else
                        return response.createError()
                            .flux()
                            .cast(MoneybirdLedgerAccount.class);
                });
    }

    /*@Override
    public Mono<MoneybirdLedgerAccount> createLedger(MoneybirdLedgerAccount ledger) {
        wrappedLedger.setLedgerAccount(ledger);

        return webClientWithBaseUrl.post()
                .uri("/ledger_accounts.json")
                .body(BodyInserters.fromValue(wrappedLedger))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.CREATED) {
                        return response.bodyToMono(MoneybirdLedgerAccount.class);
                    }
                    else return response.createError();
                });
    }*/

    // Checks if there are any ledgers containing
    // a customer ISO country in their names
    @Override
    public String getLedgerId(String customerIsoCountry) {
        Iterable<MoneybirdLedgerAccount> addedLedgers = getAllLedgers()
                .toIterable();
        String id = null;
        String defaultLedgerId = null;

        for (MoneybirdLedgerAccount addedLedger : addedLedgers) {
            String addedLedgerName = addedLedger.getName().toLowerCase();
            String formattedCustomerCountry = "("
                    + customerIsoCountry.toLowerCase()
                    + ")";

            // If the added MB ledger contains a '(isoCountryName)' in its
            // name, then get its id
            if (addedLedgerName.contains(formattedCustomerCountry)) {
                id = addedLedger.getId();
                if (addedLedger.getName().contains("other countries"))
                    defaultLedgerId = id;
                break;
            }
        }

        // returns the id of a particular ledger found or a default one otherwise
        if (id != null)
            return id;
        else {
            return defaultLedgerId;
        }
    }



    @Component
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class LedgerWrapper {
        MoneybirdLedgerAccount ledgerAccount;
    }
}
