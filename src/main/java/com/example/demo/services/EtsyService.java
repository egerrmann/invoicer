package com.example.demo.services;

import com.example.demo.models.etsy.EtsyLedger;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.etsy.EtsyShop;
import com.example.demo.models.etsy.oauth2.AccessTokenReceivedEvent;
import com.example.demo.models.etsy.oauth2.EtsyOAuthProperties;
import com.example.demo.models.etsy.EtsyUser;
import com.example.demo.models.etsy.responses.GetEtsyList;
import com.example.demo.services.interfaces.IEtsyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

import static java.lang.Float.NaN;

@Service
public class EtsyService implements IEtsyService {

    private WebClient webClient;
    private final EtsyOAuthProperties properties;
    private EtsyUser user;
    private EtsyShop shop;

    public EtsyService(EtsyOAuthProperties properties, EtsyUser user) {
        this.properties = properties;
        this.webClient = WebClient.create();
        this.user = user;
    }

//    @Override
//    public void tryOauth() {
//        Mono<EtsyUser> resp = webClient.get()
//                .uri("https://openapi.etsy.com/v3/application/users/" + user.getUserId())
//                .retrieve()
//                .bodyToMono(EtsyUser.class);
//        resp.subscribe(System.out::println, error -> {
//            System.out.println(error.toString());
//        });
//    }
//
//    @Override
//    public Mono<EtsyShop> getShop() {
//        Mono<EtsyShop> resp = webClient.get()
//                .uri("https://openapi.etsy.com/v3/application/users/me")
//                .retrieve()
//                .bodyToMono(EtsyShop.class);
//        resp.subscribe(System.out::println, error -> {
//            System.out.println(error.toString());
//        });
//
//        return resp;
//    }

    @Override
    public Flux<EtsyReceipt> getReceipts() {
        Mono<GetEtsyList> resp = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        // For some reason WebClient throws an error
                        // if in uriBuilder in path() you have "https://",
                        // so I moved it to the base url, and it worked
                        .path("shops/{shopId}/receipts")
                        .queryParam("limit", 1)
                        .build(shop.getShopId()))
                .retrieve()
                .bodyToMono(GetEtsyList.class);
        resp.subscribe(System.out::println,error -> {
            System.out.println(error.toString());
        });
        List<EtsyReceipt> receipts = (List<EtsyReceipt>) resp.block().getResults();
        Flux<EtsyReceipt> fluxReceipts = Flux.fromIterable(receipts);
        return fluxReceipts;
    }

    @Override
    public Flux<EtsyLedger> getLedgers() {

//        byte[] bytes = new byte[]{123, 34, 101, 114, 114, 111, 114, 34, 58, 34, 84, 105, 109, 101, 32, 119, 105, 110, 100, 111, 119, 32, 98, 101, 116, 119, 101, 101, 110, 32, 109, 105, 110, 95, 99, 114, 101, 97, 116, 101, 100, 32, 97, 110, 100, 32, 109, 97, 120, 95, 99, 114, 101, 97, 116, 101, 100, 32, 109, 117, 115, 116, 32, 98, 101, 32, 110, 111, 32, 109, 111, 114, 101, 32, 116, 104, 97, 110, 32, 50, 54, 55, 56, 52, 48, 48, 32, 115, 101, 99, 111, 110, 100, 115, 32, 40, 51, 49, 32, 100, 97, 121, 115, 41, 46, 34, 125, 10};
//        String string = new String(bytes);
//        System.out.println("\n |||||||||||||||||||||||||| \n");
//        System.out.println(string);
//        System.out.println("\n |||||||||||||||||||||||||| \n");


        // These values are for test purposes only.
        // Later on they should be retrieved / calculated with
        // the data from the database (e.g. lastUpdated)
        // TODO Change test values to the normal logic
        Date today = new Date();
        Long monthAgo = today.getTime() - 2678400;

        Mono<GetEtsyList> resp = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("shops/{shopId}/payment-account/ledger-entries")
                        // TODO Fix the *_created params so that the call to the Etsy passed
                        .queryParam("min_created", monthAgo)
                        .queryParam("max_created", today.getTime())
//                        .queryParam("limit", 1)
//                        .queryParam("offset", 0)
                        .build(shop.getShopId()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, err -> {
                    err.bodyToMono(String.class)
                            .subscribe(System.out::println);

                    // TODO Make proper error-handling.

                    throw new RuntimeException();
                })
                .bodyToMono(GetEtsyList.class);
        resp.subscribe(System.out::println,error -> {
            System.out.println(error.getLocalizedMessage());
        });
        List<EtsyLedger> ledgers = (List<EtsyLedger>) resp.block().getResults();
        Flux<EtsyLedger> fluxLedgers = Flux.fromIterable(ledgers);
        return fluxLedgers;
    }

    @Override
    public void onApplicationEvent(AccessTokenReceivedEvent event) {
        this.user = event.getUser();
        this.shop = event.getShop();
        this.webClient = WebClient.builder()
                .defaultHeader("x-api-key", properties.getRegistration().getEtsy().getClientId())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + event.getUser().getAccessToken())
                .baseUrl("https://openapi.etsy.com/v3/application/")
                .build();
    }
}
