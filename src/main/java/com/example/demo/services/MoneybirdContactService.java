package com.example.demo.services;

import com.example.demo.models.moneybird.MoneybirdContact;
import com.example.demo.repositories.IContactRepository;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoneybirdContactService implements IMoneybirdContactService {
    private final WebClient webClientWithBaseUrl;
    private final ContactWrapper wrappedContact;
    private final IContactRepository contactRepository;
    private final RateLimiter moneybirdRateLimiter;

    // TODO: move this method to the test class
    public MoneybirdContact getTestContact() {
        MoneybirdContact contact = new MoneybirdContact();
        contact.setCompanyName("Test company name");
        contact.setAddress1("NL, Test st, apt. 67");
        contact.setPhone("+375291234567");
        return contact;
    }

    @Override
    public List<MoneybirdContact> getAllContacts() {
        List<MoneybirdContact> allContacts = new ArrayList<>();
        boolean isPageWithContactsEmpty = false;
        int pageNumber = 0;

        while (!isPageWithContactsEmpty) {
            List<MoneybirdContact> contactsFromOnePage = get100Contacts(++pageNumber)
                    .toStream()
                    // contact should be a natural person
                    .filter(moneybirdContact -> moneybirdContact.getFullName() != null
                            && !moneybirdContact.getFullName().isEmpty())
                    .toList();
            if (!contactsFromOnePage.isEmpty()) {
                System.out.println(pageNumber + ") " + contactsFromOnePage.get(0).getFullName());
                allContacts.addAll(contactsFromOnePage);
            }
            else {
                isPageWithContactsEmpty = true;
                System.out.println("page with contacts is empty");
            }
        }

        return allContacts;
    }

    @Override
    public Flux<MoneybirdContact> get100Contacts(int pageNumber) {
        return webClientWithBaseUrl.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/contacts.json")
                        .queryParam("per_page", 100)
                        .queryParam("page", pageNumber)
                        .build())
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToFlux(MoneybirdContact.class);
                    else return response.createError().flux().cast(MoneybirdContact.class);
                })
                .transformDeferred(RateLimiterOperator.of(moneybirdRateLimiter));
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
                })
                .transformDeferred(RateLimiterOperator.of(moneybirdRateLimiter));
    }

    @Override
    public Long getContactId(MoneybirdContact contact) {
        Optional<String> contactFullName = contact.getOptionalFullName();

        Iterable<MoneybirdContact> addedContacts = getAllContacts();
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
                })
                .transformDeferred(RateLimiterOperator.of(moneybirdRateLimiter));
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
