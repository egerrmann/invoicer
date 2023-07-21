package com.example.demo.services.interfaces;

import com.example.demo.models.moneybird.MoneybirdContact;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IMoneybirdContactService {
    List<MoneybirdContact> getAllContacts();
    Flux<MoneybirdContact> get100Contacts(int pageNumber);
    Mono<MoneybirdContact> getContactById(String id);
    Long getContactId(MoneybirdContact contact);
    Mono<MoneybirdContact> createContact(MoneybirdContact contact);
}
