package com.example.demo.services.interfaces;

import com.example.demo.models.moneybird.MoneybirdLedgerAccount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMoneybirdLedgerAccountService {
    Flux<MoneybirdLedgerAccount> getAllLedgers();
    String getLedgerId(String customerIsoCountry);
}
