package com.example.demo.services;

import com.example.demo.controllers.MoneybirdController;
import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.SalesInvoice;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneybirdContactServiceTest {
    private static MockWebServer server;
    private MoneybirdController controller = new MoneybirdController();

    final static Dispatcher dispatcher = new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

            switch (request.getPath()) {
                case "/moneybird/contacts":
                    return new MockResponse()
                            .setResponseCode(201);
            }
            return new MockResponse().setResponseCode(404);
        }
    };

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        //server.setDispatcher(dispatcher);
        server.start(8080);
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                server.getPort());
        controller.setContactService(new MoneybirdContactService(baseUrl));
    }

    @Test
    public void createContactTest() throws Exception {
        MoneybirdContact contact = new MoneybirdContact();
        contact.setCompanyName("Test company name");
        contact.setAddress1("NL, Test st, apt. 67");
        contact.setPhone("+375291234567");

        server.enqueue(new MockResponse().setBody("hello, world!"));
        controller.createContact(contact);
        RecordedRequest request = server.takeRequest();

        assertEquals("/contacts.json", request.getPath());
        assertEquals("GET", request.getMethod());
        assertEquals("dfr", request.getRequestUrl());

    }

    @Test
    public void getAllContactsTest() throws Exception {
        server.enqueue(new MockResponse().setBody("hello, world!"));
        controller.getAllContacts();
        RecordedRequest request = server.takeRequest();

        assertEquals("/contacts.json", request.getPath());
        assertEquals("GET", request.getMethod());
        assertEquals("df", request.getBody());
        assertEquals("dfr", request.getRequestUrl());

    }

    /*@Test
    public void getAllContactsTest() throws Exception {
        given(service.getAllContacts()).willReturn(Flux.just(getTestContact()));

        StepVerifier.create(service.getAllContacts())
                .expectNext(getTestContact())
                .verifyComplete();
    }*/

    private MoneybirdContact getTestContact() {
        MoneybirdContact contact = new MoneybirdContact();
        contact.setCompanyName("Test company name");
        contact.setAddress1("NL, Test st, apt. 67");
        contact.setPhone("+375291234567");
        return contact;
    }

    private SalesInvoice getTestInvoice() {
        SalesInvoice invoice = new SalesInvoice();
        invoice.setReference("30052");
        invoice.setContactId(new BigInteger("380279277811139756"));
        //invoice.setDiscount(15.5);

        SalesInvoice.DetailsAttributes detailsAttributes =
                new SalesInvoice.DetailsAttributes();
        detailsAttributes.setDescription("My own chair");
        detailsAttributes.setPrice(129.95);
        invoice.getDetailsAttributes().add(detailsAttributes);

        return invoice;
    }
}
