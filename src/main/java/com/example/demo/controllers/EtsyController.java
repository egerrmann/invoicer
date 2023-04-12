package com.example.demo.controllers;


import com.example.demo.models.etsy.EtsyLedger;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.services.interfaces.IEtsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/etsy")
public class EtsyController {
    private IEtsyService authService;

    @Autowired
    private void setService(IEtsyService authService) {
        this.authService = authService;
    }

    @GetMapping("/receipts")
    public ResponseEntity<List<EtsyReceipt>> getReceipts() {
        List<EtsyReceipt> resp = authService.getReceiptsList();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/ledgers")
    public ResponseEntity<Flux<EtsyLedger>> getLedgers() {
        Flux<EtsyLedger> resp = authService.getLedgers();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
