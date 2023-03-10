package com.example.demo.controllers;

import com.example.demo.models.CallbackData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login/oauth2/code")
public class AuthController {


    @GetMapping
    public void getCallback(@RequestBody CallbackData data) {
        System.out.println("Entering callback method");
    }
}
