package com.example.demo.services;

import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.etsy.EtsyShop;
import com.example.demo.models.etsy.oauth2.AccessTokenReceivedEvent;
import com.example.demo.models.etsy.oauth2.EtsyOAuthProperties;
import com.example.demo.models.etsy.EtsyUser;
import com.example.demo.models.etsy.responses.GetReceipts;
import com.example.demo.services.interfaces.IEtsyAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
public class EtsyAuthService implements IEtsyAuthService {

    private WebClient webClient;
    private final EtsyOAuthProperties properties;
    private EtsyUser user;
    private EtsyShop shop;

    public EtsyAuthService(EtsyOAuthProperties properties, EtsyUser user) {
        this.properties = properties;
        this.webClient = WebClient.create();
        this.user = user;
    }

    @Override
    public void tryOauth() {
        Mono<EtsyUser> resp = webClient.get()
                // 752184607
//                .uri("https://openapi.etsy.com/v3/application/users/" + user.getUserId())
                .uri("https://openapi.etsy.com/v3/application/users/" + "752184607")
                .retrieve()
                .bodyToMono(EtsyUser.class);
        resp.subscribe(System.out::println, error -> {
            System.out.println(error.toString());
        });
    }

    @Override
    public Mono<EtsyShop> getShop() {
        Mono<EtsyShop> resp = webClient.get()
                .uri("https://openapi.etsy.com/v3/application/users/me")
                .retrieve()
                .bodyToMono(EtsyShop.class);
        resp.subscribe(System.out::println, error -> {
            System.out.println(error.toString());
        });

        return resp;
    }

    @Override
    // Returns nothing at this moment.
    // Probably the problem is in the lack of receipts
    public Flux<EtsyReceipt> getReceipts() {
        Mono<GetReceipts> resp = webClient.get()
                .uri("https://openapi.etsy.com/v3/application/shops/" + shop.getShopId() + "/receipts")
                .attribute("limit", 1)
                .retrieve()
                .bodyToMono(GetReceipts.class);
        resp.subscribe(System.out::println,error -> {
            System.out.println(error.toString());
        });
        List<EtsyReceipt> receipts = resp.block().getResults();
        Flux<EtsyReceipt> fluxReceipts = Flux.fromIterable(receipts);
        return fluxReceipts;
    }


    @Override
    public void onApplicationEvent(AccessTokenReceivedEvent event) {
        this.user = event.getUser();
        this.shop = event.getShop();
        this.webClient = WebClient.builder()
                .defaultHeader("x-api-key", properties.getRegistration().getEtsy().getClientId())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + event.getUser().getAccessToken())
                .build();
    }
}
