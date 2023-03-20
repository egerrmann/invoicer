package com.example.demo.services.interfaces;

import com.example.demo.models.etsy.oauth2.AccessTokenReceivedEvent;
import org.springframework.context.ApplicationListener;

public interface IEtsyConnect extends ApplicationListener<AccessTokenReceivedEvent> {

}
