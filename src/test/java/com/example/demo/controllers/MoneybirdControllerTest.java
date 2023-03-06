package com.example.demo.controllers;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.services.MoneybirdContactService;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MoneybirdControllerTest {
    public static MockWebServer server;

    final static Dispatcher dispatcher = new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

            switch (request.getPath()) {
                case "/contacts.json":
                    return new MockResponse()
                            .setResponseCode(200);
            }
            return new MockResponse().setResponseCode(404);
        }
    };
    private MoneybirdController controller = new MoneybirdController();

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.setDispatcher(dispatcher);
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

        Flux<MoneybirdContact> apiResponse = controller.getAllContacts().getBody();
        StepVerifier.create(apiResponse)
                .expectNext(contact)
                .verifyComplete();
        // server.enqueue(new MockResponse().setBody("hello, world!"));
        RecordedRequest request = server.takeRequest();

        assertEquals("/contacts", request.getPath());

    }

    @Test
    public void createInvoiceTest() throws Exception {

    }
}
