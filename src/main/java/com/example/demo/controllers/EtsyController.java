package com.example.demo.controllers;

import com.example.demo.models.oauth2.EtsyOAuthProperties;
import com.example.demo.services.interfaces.IEtsyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.http.HttpStatus;
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
@AutoConfiguration
@RequestMapping("/etsy")
public class EtsyController {
    private IEtsyAuthService authService;
    private WebClient webClient;
    private EtsyOAuthProperties properties;
//    private AuthorizationRequestRepository requestRepository;

    @Autowired
    private void setService(IEtsyAuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setWebClient(WebClient webClient) { this.webClient = webClient; }

    @Autowired
    public void setProperties(EtsyOAuthProperties properties) { this.properties = properties; }
//    @Autowired
//    private void setRequestRepository(AuthorizationRequestRepository repository) {requestRepository = repository}

//    @GetMapping
//    public ResponseEntity<String> getAuthToken() {
//        String data = null;
//
//        try {
//            data = authService.getOAuthToken();
//        } catch (Error err) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity<>(data, HttpStatus.OK);
//    }

//    @GetMapping
//    public void tryAuth(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
//        System.out.println("Entering webclient");
//        webClient
//                .get()
//                .uri("https://openapi.etsy.com/v3/application/users/me")
//                .attributes(clientRegistrationId("etsy"))
//                .attributes(oauth2AuthorizedClient(client))
//                .header("x-api-key", properties.getRegistration().getEtsy().getClientId())
//                .retrieve()
//                .toEntity(String.class)
//                .subscribe(data -> {
//                    System.out.println("The status code: " + data.getStatusCode());
//                    System.out.println("The body that I get: " + data.getBody());
//                }, error -> {
//                    System.out.println("Error message: " + error.getMessage());
//                });
//        System.out.println("Leaving webclient");
//    }

}
