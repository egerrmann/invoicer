package com.example.demo.controllers;

import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class MoneybirdControllerTest {
    private MockMvc mockMvc;

    @Mock
    private IMoneybirdInvoiceService invoiceService;

    @Mock
    private IMoneybirdContactService contactService;

    @InjectMocks
    private MoneybirdController controller;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void createInvoiceTest() throws Exception {

    }

    @Test
    public void getAllInvoicesTest() throws Exception {
        SalesInvoice invoice = new SalesInvoice();
        invoice.setReference("30052");
        invoice.setContactId(new BigInteger("380279277811139756"));
        //invoice.setDiscount(15.5);

        SalesInvoice.DetailsAttributes detailsAttributes =
                new SalesInvoice.DetailsAttributes();
        detailsAttributes.setDescription("My own chair");
        detailsAttributes.setPrice(129.95);
        invoice.getDetailsAttributes().add(detailsAttributes);

        given(invoiceService.getAllInvoices()).willReturn(Flux.just(invoice));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .get("/moneybird/invoices")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        StepVerifier.create(invoiceService.getAllInvoices())
                .expectNext(invoice)
                .verifyComplete();
        //assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//                .andExpect(jsonPath("$[0].contactId", is(new BigInteger("380279277811139756"))));
    }
}
