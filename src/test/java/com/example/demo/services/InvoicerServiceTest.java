package com.example.demo.services;

import com.example.demo.models.exceptions.IncorrectDataException;
import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.services.interfaces.IEtsyService;
import com.example.demo.services.interfaces.IMoneybirdTaxRatesService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// TODO Make sure it's InvoicerServiceTest, but not TaxRatesServiceTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class InvoicerServiceTest {
    @MockBean
    private IEtsyService etsyService;

    @Spy
    private IMoneybirdTaxRatesService taxRatesService;

    @BeforeAll
    public void init() {
        taxRatesService = new MoneybirdTaxRatesService(null, etsyService, null);
    }

    @Test
    public void testGetMaxTaxIfShopAndCustomerAreOfTheDomesticCountry() {
        given(etsyService.getShopIso()).willReturn("DU");
        doReturn(getTestTaxRates().filter(rate -> rate.getCountry() == null))
                .when(taxRatesService).getDomesticTaxRates();

        MoneybirdTaxRate taxRate = taxRatesService.getMaxCountryTax("DU");

        assertEquals("21.0", taxRate.getPercentage());
        assertNull(taxRate.getCountry());
    }

    @Test
    public void testGetMaxTaxIfShopIsDutchAndCustomerIsGerman() {
        given(etsyService.getShopIso()).willReturn("DU");
        doReturn(getTestTaxRates()
                .filter(rate -> rate.getCountry() != null
                        && rate.getCountry().equals("DE")))
                .when(taxRatesService).getAllTaxRates("DE");

        MoneybirdTaxRate taxRate = taxRatesService.getMaxCountryTax("DE");

        assertEquals("19.0", taxRate.getPercentage());
        assertEquals("DE", taxRate.getCountry());
    }

    @Test
    public void testGetMaxTaxIfCustomerCountryIsNotAddedToMoneybird() {
        given(etsyService.getShopIso()).willReturn("DU");
        doReturn(getTestTaxRates()
                .filter(rate -> rate.getCountry() != null
                        && rate.getCountry().equals("BY")))
                .when(taxRatesService).getAllTaxRates("BY");
        doReturn(getTestTaxRates().filter(rate -> rate.getCountry() == null))
                .when(taxRatesService).getDomesticTaxRates();

        MoneybirdTaxRate taxRate = taxRatesService.getMaxCountryTax("BY");

        assertEquals("21.0", taxRate.getPercentage());
        assertNull(taxRate.getCountry());
    }

    @Test
    public void testGetMaxTaxRateThrowsException() {
        given(etsyService.getShopIso()).willReturn("DU");
        doReturn(Flux.just()).when(taxRatesService).getDomesticTaxRates();

        IncorrectDataException thrown = assertThrows(IncorrectDataException.class,
                () -> taxRatesService.getMaxCountryTax("DU"),
                "Couldn't find max tax rate on Moneybird");
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    private Flux<MoneybirdTaxRate> getTestTaxRates() {
        MoneybirdTaxRate domesticTaxRate1 = new MoneybirdTaxRate();
        domesticTaxRate1.setPercentage("0.0");

        MoneybirdTaxRate domesticTaxRate2 = new MoneybirdTaxRate();
        domesticTaxRate2.setPercentage("9.0");

        MoneybirdTaxRate domesticTaxRate3 = new MoneybirdTaxRate();
        domesticTaxRate3.setPercentage("21.0");


        MoneybirdTaxRate germanTaxRate1 = new MoneybirdTaxRate();
        germanTaxRate1.setCountry("DE");
        germanTaxRate1.setPercentage("7.0");

        MoneybirdTaxRate germanTaxRate2 = new MoneybirdTaxRate();
        germanTaxRate2.setCountry("DE");
        germanTaxRate2.setPercentage("19.0");


        MoneybirdTaxRate frenchTaxRate1 = new MoneybirdTaxRate();
        frenchTaxRate1.setCountry("FR");
        frenchTaxRate1.setPercentage("2.1");

        MoneybirdTaxRate frenchTaxRate2 = new MoneybirdTaxRate();
        frenchTaxRate2.setCountry("FR");
        frenchTaxRate2.setPercentage("5.5");

        MoneybirdTaxRate frenchTaxRate3 = new MoneybirdTaxRate();
        frenchTaxRate3.setCountry("FR");
        frenchTaxRate3.setPercentage("10.0");

        MoneybirdTaxRate frenchTaxRate4 = new MoneybirdTaxRate();
        frenchTaxRate4.setCountry("FR");
        frenchTaxRate4.setPercentage("20.0");


        MoneybirdTaxRate hungarianTaxRate1 = new MoneybirdTaxRate();
        hungarianTaxRate1.setCountry("HU");
        hungarianTaxRate1.setPercentage("5.0");

        MoneybirdTaxRate hungarianTaxRate2 = new MoneybirdTaxRate();
        hungarianTaxRate2.setCountry("HU");
        hungarianTaxRate2.setPercentage("18.0");

        MoneybirdTaxRate hungarianTaxRate3 = new MoneybirdTaxRate();
        hungarianTaxRate3.setCountry("HU");
        hungarianTaxRate3.setPercentage("27.0");


        return Flux.just(domesticTaxRate1,
                domesticTaxRate2,
                domesticTaxRate3,
                germanTaxRate1,
                germanTaxRate2,
                frenchTaxRate1,
                frenchTaxRate2,
                frenchTaxRate3,
                frenchTaxRate4,
                hungarianTaxRate1,
                hungarianTaxRate2,
                hungarianTaxRate3);
    }
}
