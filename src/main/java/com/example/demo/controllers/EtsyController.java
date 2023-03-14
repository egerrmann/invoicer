package com.example.demo.controllers;

import com.example.demo.models.AccessTokenReceivedEvent;
import com.example.demo.models.oauth2.EtsyOAuthProperties;
import com.example.demo.models.oauth2.EtsyUser;
import com.example.demo.services.interfaces.IEtsyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
@RequestMapping("/etsy")
public class EtsyController implements ApplicationListener<AccessTokenReceivedEvent> {
    private IEtsyAuthService authService;
    private WebClient webClient;
    private EtsyOAuthProperties properties;
    private EtsyUser user;
//    private AuthorizationRequestRepository requestRepository;

    @Autowired
    private void setService(IEtsyAuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setWebClient(WebClient etsyWebClient) { this.webClient = etsyWebClient; }

    @Autowired
    public void setProperties(EtsyOAuthProperties properties) { this.properties = properties; }

    @Autowired
    public void setUser(EtsyUser user) { this.user = user; }

    @Autowired
//    private void setRequestRepository(AuthorizationRequestRepository repository) {requestRepository = repository}

    @GetMapping("/try")
    public void tryAuth() {
        System.out.println("Entering webclient");
        Mono<EtsyUser> resp = webClient.get()
                .uri("https://openapi.etsy.com/v3/application/users/" + user.getUserId())
                .retrieve()
                .bodyToMono(EtsyUser.class);
        resp.subscribe(System.out::println, error -> {
            System.out.println(error.toString());
        });
        System.out.println("Leaving webclient");
    }

    @Override
    public void onApplicationEvent(AccessTokenReceivedEvent event) {
        this.user = event.getUser();
        this.webClient = WebClient.builder()
                .defaultHeader("x-api-key", properties.getRegistration().getEtsy().getClientId())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + event.getUser().getAccessToken())
                .build();
    }
}
