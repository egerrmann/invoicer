package com.example.demo.models.etsy;

import com.example.demo.models.etsy.oauth2.EtsyOAuthProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

//@Component
// This class is not used at the moment, but
// it may come in handy later
public class EtsyWebClient {
    private WebClient webclient;
    private String accessToken;
    private EtsyOAuthProperties properties;

    public EtsyWebClient(EtsyOAuthProperties properties) {
        this.properties = properties;
        this.webclient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-api-key", properties.getRegistration().getEtsy().getClientId())
                .build();
    }

//    public void
}
