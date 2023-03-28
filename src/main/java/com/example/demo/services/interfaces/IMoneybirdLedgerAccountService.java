package com.example.demo.services.interfaces;

import com.example.demo.models.moneybird.MoneybirdLedgerAccount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMoneybirdLedgerAccountService {
    Flux<MoneybirdLedgerAccount> getAllLedgers();
    Mono<MoneybirdLedgerAccount> createLedger(MoneybirdLedgerAccount ledger);
//    String getLedgerId(MoneybirdLedgerAccount ledger);
}
