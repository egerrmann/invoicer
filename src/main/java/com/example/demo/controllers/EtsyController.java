package com.example.demo.controllers;


import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.etsy.EtsyShop;
import com.example.demo.services.interfaces.IEtsyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/etsy")
public class EtsyController {
    private IEtsyAuthService authService;

    @Autowired
    private void setService(IEtsyAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/user")
    public void tryAuth() {
        authService.tryOauth();
    }

    @GetMapping("/me")
    public ResponseEntity<Mono<EtsyShop>> getShop() {
        Mono<EtsyShop> resp = authService.getShop();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
//        return resp;
    }

    @GetMapping("/receipts")
    public ResponseEntity<Flux<EtsyReceipt>> getReceipts() {
        Flux<EtsyReceipt> resp = authService.getReceipts();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
//        return resp;
    }
}
