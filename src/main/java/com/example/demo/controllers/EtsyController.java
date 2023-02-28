package com.example.demo.controllers;

import com.example.demo.services.interfaces.IEtsyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AutoConfiguration
@RequestMapping("/etsy")
public class EtsyController {
    private IEtsyAuthService authService;
//    private AuthorizationRequestRepository requestRepository;

    @Autowired
    private void setService(IEtsyAuthService authService) {
        this.authService = authService;
    }

//    @Autowired
//    private void setRequestRepository(AuthorizationRequestRepository repository) {requestRepository = repository}

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

    @GetMapping
    public void tryAuth() {

    }
}
