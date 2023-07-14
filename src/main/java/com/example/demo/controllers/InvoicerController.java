package com.example.demo.controllers;

import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.services.InvoicerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// TODO: change the URL (to "/index" I suppose)
@RequestMapping("/invoicer")
@RequiredArgsConstructor
public class InvoicerController {
    private final InvoicerService service;

    @GetMapping("/add-invoices")
    public ResponseEntity<List<SalesInvoice>> addAllInvoices(
            @RequestParam(name = "start-date") String startDate,
            @RequestParam(name = "end-date") String endDate) {

        // TODO I suppose we have to leave only 'try'-block, because
        //  CustomExceptionHandler automatically returns a correct response page depending
        //  on an exception thrown and there is no need to handle it over here
        //try {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.createInvoices(startDate, endDate));
        /*} catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of());
        }*/
    }
}
