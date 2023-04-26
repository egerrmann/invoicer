package com.example.demo.services;

import com.example.demo.models.etsy.EtsyLedger;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.etsy.EtsyShop;
import com.example.demo.models.etsy.oauth2.AccessTokenReceivedEvent;
import com.example.demo.models.etsy.oauth2.OAuthProperties;
import com.example.demo.models.etsy.EtsyUser;
import com.example.demo.models.etsy.responses.GetLedgerList;
import com.example.demo.models.etsy.responses.GetReceiptList;
import com.example.demo.services.interfaces.IEtsyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
public class EtsyService implements IEtsyService {

    private WebClient webClient;
    private final OAuthProperties properties;
    private EtsyUser user;
    private EtsyShop shop;

    @Value("${etsy.base-url}")
    private String baseUrl;

    public EtsyService(OAuthProperties properties, EtsyUser user) {
        this.properties = properties;
        this.webClient = WebClient.create();
        this.user = user;
    }

    @Override
    public Mono<GetReceiptList> getReceipts() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("shops/{shopId}/receipts")
                        // these params will let us receive the needed receipts
                        .queryParam("limit", 50)
//                        .queryParam("limit", 4)
                        .queryParam("min_created", 1677628800)
                        .queryParam("max_created", 1680307199)
                        .build(shop.getShopId()))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToMono(GetReceiptList.class);
//                            else if (response.statusCode().isError()) {
//                                response.bodyToMono(String.class)
//                                        .subscribe(System.out::println);
//                                return null;
//                            }
                    else return response.createError();
                });
    }

    @Override
    public List<EtsyReceipt> getReceiptsList() {
        return getReceipts()
                .block()
                .getResults();
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

        Mono<GetLedgerList> resp = webClient
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
                .bodyToMono(GetLedgerList.class);
        resp.subscribe(null,error -> {
            System.out.println(error.getLocalizedMessage());
        });
        List<EtsyLedger> ledgers = resp.block().getResults();
        Flux<EtsyLedger> fluxLedgers = Flux.fromIterable(ledgers);
        return fluxLedgers;
    }

    @Override
    public void onApplicationEvent(AccessTokenReceivedEvent event) {
        this.user = event.getUser();
        this.shop = event.getShop();

        // TODO: check if there are any security concerns
        //  when the WebClient buffer is manually increased

        // increase a buffer size in order to
        // process a large number of receipts from Etsy
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        this.webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .defaultHeader("x-api-key", properties.getRegistration().getEtsy().getClientId())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + event.getUser().getAccessToken())
                .baseUrl(baseUrl)
                .build();
    }
}
