package com.example.demo.controllers;

import com.example.demo.models.EtsyOAuth;
import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IEtsyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/etsy")
public class EtsyController {
    private IEtsyAuthService authService;

    @Autowired
    private void setService(IEtsyAuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<String> getAuthToken() {
        String data = null;

        try {
            data = authService.getOAuthToken();
        } catch (Error err) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
