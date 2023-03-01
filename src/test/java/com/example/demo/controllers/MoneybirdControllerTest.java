package com.example.demo.controllers;

import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@WebFluxTest(MoneybirdController.class)
public class MoneybirdControllerTest {
    @Autowired
    private WebTestClient client;

    @MockBean
    private IMoneybirdInvoiceService service;

    @Test
    public void createNewInvoiceTest() throws Exception {

    }
}
