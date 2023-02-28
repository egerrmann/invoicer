package com.example.demo.models.oauth2;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
@Component
@Getter
public class EtsyOAuthProperties {
    private Registration registration;
    private Provider provider;

    @Getter
    public static class Registration {
        private Etsy etsy;

        @Getter
        public static class Etsy {
            private String clientId;
            private String clientAuthenticationMethod;
            private String authorizationGrantType;
            private String redirectUri;
            private String scope;
            private String clientName;
        }
    }

    @Getter
    public static class Provider {
        private Etsy etsy;

        @Getter
        public static class Etsy {
            private String authorizationUri;
            private String tokenUri;
        }
    }
}
