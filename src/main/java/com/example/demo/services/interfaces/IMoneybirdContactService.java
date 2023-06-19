package com.example.demo.services.interfaces;

import com.example.demo.models.moneybird.MoneybirdContact;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMoneybirdContactService {
    Flux<MoneybirdContact> getAllContacts();
    Mono<MoneybirdContact> getContactById(String id);
    Long getContactId(MoneybirdContact contact);
    Mono<MoneybirdContact> createContact(MoneybirdContact contact);
}
