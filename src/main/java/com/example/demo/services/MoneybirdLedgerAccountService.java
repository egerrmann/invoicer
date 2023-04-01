package com.example.demo.services;

import com.example.demo.models.moneybird.MoneybirdLedgerAccount;
import com.example.demo.services.interfaces.IMoneybirdLedgerAccountService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class MoneybirdLedgerAccountService implements IMoneybirdLedgerAccountService {
    private final WebClient webClientWithBaseUrl;
    private final LedgerWrapper wrappedLedger;

    public MoneybirdLedgerAccountService(WebClient webClientWithBaseUrl,
                                         LedgerWrapper wrappedLedger) {
        this.webClientWithBaseUrl = webClientWithBaseUrl;
        this.wrappedLedger = wrappedLedger;
    }

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

    @Override
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
    }

    @Override
    public String getLedgerId(MoneybirdLedgerAccount ledger) {
        String ledgerName = ledger.getName();

        Iterable<MoneybirdLedgerAccount> addedLedgers = getAllLedgers()
                .toIterable();
        String id = null;

        for (MoneybirdLedgerAccount addedLedger : addedLedgers) {
            String addedLedgerName = addedLedger.getName();

            if (Objects.equals(addedLedgerName, ledgerName)) {
                id = addedLedger.getId();
                break;
            }
        }

        return id;
    }

    @Component
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class LedgerWrapper {
        MoneybirdLedgerAccount ledgerAccount;
    }
}
