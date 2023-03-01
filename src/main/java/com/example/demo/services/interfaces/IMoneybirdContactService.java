package com.example.demo.services.interfaces;

import com.example.demo.models.MoneybirdContact;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMoneybirdContactService {
    MoneybirdContact getTestContact();
    Flux<MoneybirdContact> getAllContacts();
    Mono<MoneybirdContact> createNewContact(MoneybirdContact contact);
}
