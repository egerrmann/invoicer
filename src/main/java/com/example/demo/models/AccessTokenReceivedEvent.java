package com.example.demo.models;

import com.example.demo.models.oauth2.EtsyUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class AccessTokenReceivedEvent extends ApplicationEvent {
    @Getter
    @Setter
    private EtsyUser user;

    public AccessTokenReceivedEvent(EtsyUser user) {
        super(user);
        this.user = user;
    }

    public AccessTokenReceivedEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
