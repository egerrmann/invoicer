package com.example.demo.services.interfaces;

import com.example.demo.models.etsy.EtsyLedger;
import com.example.demo.models.etsy.EtsyReceipt;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IEtsyService extends IEtsyConnect {
    List<EtsyReceipt> getReceipts(String startDate, String endDate);
    Flux<EtsyLedger> getLedgers();
    String getShopIso();
}
