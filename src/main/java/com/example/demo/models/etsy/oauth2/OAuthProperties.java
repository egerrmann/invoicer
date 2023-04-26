package com.example.demo.models.etsy.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
@Component
@Getter
@Setter
public class OAuthProperties {
    private Registration registration;
    private Provider provider;

    @Getter
    @Setter
    public static class Registration {
        private RegistrationIdProperties etsy;
        private RegistrationIdProperties moneybird;
    }

    @Getter
    @Setter
    public static class Provider {
        private ProviderIdProperties etsy;
        private ProviderIdProperties moneybird;
    }
}
