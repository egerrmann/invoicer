package com.example.demo.services;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class MoneybirdContactService implements IMoneybirdContactService {
    private WebClient webClientWithBaseUrl;
    private ContactWrapper wrappedContact;

    public MoneybirdContactService() {}

    public MoneybirdContactService(String baseUrl) {
        webClientWithBaseUrl = WebClient.create(baseUrl);
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
    public Mono<MoneybirdContact> getContactById(String id) {
        return webClientWithBaseUrl.get()
                .uri("/contacts/" + id + ".json")
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.OK) {
                        return response.bodyToMono(MoneybirdContact.class);
                    }
                    else return response.createError();
                });
    }

    @Override
    public String getContactId(MoneybirdContact contact) {
        Optional<String> contactFullName = contact.getOptionalFullName();
        Optional<String> contactCompanyName = contact.getOptionalCompanyName();

        Iterable<MoneybirdContact> addedContacts =
                getAllContacts().toIterable();
        String id = null;

        for (MoneybirdContact addedContact : addedContacts) {
            Optional<String> addedContactFullName =
                    addedContact.getOptionalFullName();
            Optional<String> addedContactCompanyName =
                    addedContact.getOptionalCompanyName();

            boolean areFullNamesEqual = contactFullName.isPresent()
                    && addedContactFullName.isPresent()
                    && addedContactFullName.get().equals(contactFullName.get());
            boolean areCompanyNamesEqual = contactCompanyName.isPresent()
                    && addedContactCompanyName.isPresent()
                    && contactCompanyName.get().equals(addedContactCompanyName.get());

            if (areFullNamesEqual || areCompanyNamesEqual) {
                id = addedContact.getId().toString();
                break;
            }
        }

        return id;
    }

    @Override
    public Mono<MoneybirdContact> createContact(MoneybirdContact contact) {
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

    @Component
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ContactWrapper {
        MoneybirdContact contact;
    }

    @Autowired
    private void setWebClientWithBaseUrl(WebClient webClientWithBaseUrl) {
        this.webClientWithBaseUrl = webClientWithBaseUrl;
    }

    @Autowired
    private void setWrappedContact(MoneybirdContactService.ContactWrapper wrappedContact) {
        this.wrappedContact = wrappedContact;
    }
}
