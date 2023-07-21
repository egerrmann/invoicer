package com.example.demo.controllers;


import com.example.demo.models.etsy.EtsyLedger;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.services.interfaces.IEtsyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/etsy")
@RequiredArgsConstructor
public class EtsyController {
    private final IEtsyService authService;

    @GetMapping("/receipts")
    public ResponseEntity<List<EtsyReceipt>> getReceipts(
            @RequestParam(name = "start-date") String startDate,
            @RequestParam(name = "end-date") String endDate) {

        List<EtsyReceipt> resp = authService.getReceipts(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/ledgers")
    public ResponseEntity<Flux<EtsyLedger>> getLedgers() {
        Flux<EtsyLedger> resp = authService.getLedgers();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
