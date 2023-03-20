package com.example.demo.services.interfaces;

import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.etsy.EtsyShop;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IEtsyAuthService extends IEtsyConnect {
    void tryOauth();
    Mono<EtsyShop> getShop();
    Flux<EtsyReceipt> getReceipts();
}
