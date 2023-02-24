package com.example.demo.controllers;

import com.example.demo.models.CallbackData;
import com.example.demo.services.interfaces.ICallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/callback")
public class CallbackController {
    private ICallbackService service;

    @Autowired
    private void setService(ICallbackService service) { this.service = service; }
    
    @PostMapping
    public void receiveCallback(@RequestBody CallbackData data) {
        service.receiveCallback(data);
    }
}
