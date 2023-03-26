package com.example.demo.models.etsy.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
@Component
@Getter
@Setter
public class EtsyOAuthProperties {
    private Registration registration;
    private Provider provider;

    @Getter
    @Setter
    public static class Registration {
        private Etsy etsy;

        @Getter
        @Setter
        public static class Etsy {
            private String clientId;
            private String clientAuthenticationMethod;
            private String authorizationGrantType;
            private String redirectUri;
            private String[] scope;
            private String clientName;
        }
    }

    @Getter
    @Setter
    public static class Provider {
        private Etsy etsy;

        @Getter
        @Setter
        public static class Etsy {
            private String authorizationUri;
            private String tokenUri;
            private String userInfoUri;
        }
    }
}
