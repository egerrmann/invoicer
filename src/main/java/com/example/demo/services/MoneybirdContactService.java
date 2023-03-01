package com.example.demo.services;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoneybirdContactService implements IMoneybirdContactService {
    private WebClient webClientWithBaseUrl;
    private ContactWrapper wrappedContact;
    @Value("${MBBearerToken}")
    private String token;

    @Override
    public MoneybirdContact getTestContact() {
        MoneybirdContact contact = new MoneybirdContact();
        contact.setCompanyName("Test company name");
        contact.setAddress1("NL, Test st, apt. 67");
        contact.setPhone("+375291234567");
        return contact;
    }

    @Override
    public Flux<MoneybirdContact> getAllContacts() {
        return webClientWithBaseUrl.get()
                .uri("/contacts.json")
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToFlux(MoneybirdContact.class);
                    else return response.createError().flux().cast(MoneybirdContact.class);
                });
    }

    @Override
    public Mono<MoneybirdContact> createNewContact(MoneybirdContact contact) {
        wrappedContact.setContact(contact);

        return webClientWithBaseUrl.post()
                .uri("/contacts.json")
                .body(BodyInserters.fromValue(wrappedContact))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.CREATED) {
                        return response.bodyToMono(MoneybirdContact.class);
                    }
                    else return response.createError();
                });
    }

    @Value("${mbApiBaseUrl}")
    private void setWebClientWithBaseUrl(String baseUrl) {
        webClientWithBaseUrl = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    @Component
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ContactWrapper {
        MoneybirdContact contact;
    }

    @Autowired
    private void setWrappedContact(MoneybirdContactService.ContactWrapper wrappedContact) {
        this.wrappedContact = wrappedContact;
    }
}
