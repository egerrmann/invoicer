package com.example.demo.services;

import com.example.demo.models.moneybird.MoneybirdContact;
import com.example.demo.repositories.IContactRepository;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class MoneybirdContactService implements IMoneybirdContactService {
    private final WebClient webClientWithBaseUrl;
    private final ContactWrapper wrappedContact;
    private final IContactRepository contactRepository;

    // TODO: move this method to the test class
    public MoneybirdContact getTestContact() {
        MoneybirdContact contact = new MoneybirdContact();
        contact.setCompanyName("Test company name");
        contact.setAddress1("NL, Test st, apt. 67");
        contact.setPhone("+375291234567");
        return contact;
    }

    @Override
    public Flux<MoneybirdContact> getAllContacts() {
        Flux<MoneybirdContact> allContacts = Flux.empty();
        Flux<MoneybirdContact> contactsFromOnePage;
        boolean isPageWithContactsEmpty = false;
        AtomicInteger pageNumber = new AtomicInteger(1);

        while (!isPageWithContactsEmpty) {
            contactsFromOnePage = webClientWithBaseUrl.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/contacts.json")
                            .queryParam("per_page", 100)
                            .queryParam("page", pageNumber.getAndIncrement())
                            .build())
                    .exchangeToFlux(response -> {
                        if (response.statusCode().equals(HttpStatus.OK))
                            return response.bodyToFlux(MoneybirdContact.class);
                        else return response.createError().flux().cast(MoneybirdContact.class);
                    });
            if (contactsFromOnePage.hasElements().block()) {
                allContacts = Flux.concat(allContacts, contactsFromOnePage);
            }
            else {
                isPageWithContactsEmpty = true;
            }
        }

        return allContacts;
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
    public Long getContactId(MoneybirdContact contact) {
        Optional<String> contactFullName = contact.getOptionalFullName();

        Iterable<MoneybirdContact> addedContacts =
                getAllContacts().toIterable();
        Long id = null;

        for (MoneybirdContact addedContact : addedContacts) {
            Optional<String> addedContactFullName =
                    addedContact.getOptionalFullName();

            boolean isFullNameTheSame = contactFullName.isPresent()
                    && addedContactFullName.isPresent()
                    && addedContactFullName.get().equals(contactFullName.get());

            if (isFullNameTheSame) {
                id = addedContact.getId();
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
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ContactWrapper {
        MoneybirdContact contact;
    }
}
