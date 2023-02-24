package com.example.demo.services;

import com.example.demo.models.CallbackData;
import com.example.demo.services.interfaces.ICallbackService;
import org.springframework.stereotype.Service;

@Service
public class CallbackService implements ICallbackService {

    @Override
    public void receiveCallback(CallbackData data) {
        System.out.println(data);
    }
}
