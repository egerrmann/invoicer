package com.example.demo.services.interfaces;

import com.example.demo.models.etsy.EtsyLedger;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.etsy.responses.GetReceiptList;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IEtsyService extends IEtsyConnect {
    Mono<GetReceiptList> getReceipts();
    Flux<EtsyLedger> getLedgers();
    List<EtsyReceipt> getReceiptsList();
    String getShopIso();
}
