package com.example.demo.services.interfaces;

import com.example.demo.models.etsy.EtsyLedger;
import com.example.demo.models.etsy.EtsyReceipt;
import reactor.core.publisher.Flux;

public interface IEtsyService extends IEtsyConnect {
    Flux<EtsyReceipt> getReceipts();
    Flux<EtsyLedger> getLedgers();
}
