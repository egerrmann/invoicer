package com.example.demo.controllers;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.BDDMockito.given;

@WebFluxTest
public class WebClientTest {
    @Autowired
    private WebTestClient client;

    @MockBean
    private IMoneybirdInvoiceService invoiceService;

    @MockBean
    private IMoneybirdContactService contactService;

    @BeforeEach
    void setUp(ApplicationContext context) {
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void test() {
        MoneybirdContact contact = new MoneybirdContact();
        contact.setCompanyName("Test company name");
        contact.setAddress1("NL, Test st, apt. 67");
        contact.setPhone("+375291234567");

//        when(contactService   .getAllContacts()).thenReturn(contact);
        given(contactService.getAllContacts()).willReturn(Flux.just(contact));

        client.get()
                .uri("/moneybird/contacts")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Flux.class).isEqualTo(Flux.just(contact));
    }
}
