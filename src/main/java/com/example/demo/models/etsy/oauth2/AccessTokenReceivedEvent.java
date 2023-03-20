package com.example.demo.models.etsy.oauth2;

import com.example.demo.models.etsy.EtsyShop;
import com.example.demo.models.etsy.EtsyUser;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class AccessTokenReceivedEvent extends ApplicationEvent {

    // May remove user and shop later, as I can just get them form the 'source'
    // But with source it'd be too much boilerplate code, so for now I'll stick to this format
    @Getter
    @Setter
    private EtsyUser user;
    @Getter
    @Setter
    private EtsyShop shop;

    public AccessTokenReceivedEvent(Pair<EtsyUser, EtsyShop> data) {
        super(data);
        this.user = data.getValue0();
        this.shop = data.getValue1();
    }

    public AccessTokenReceivedEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
