package com.example.demo.models.etsy.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Getter
@Setter
public class RegistrationIdProperties {
    private String clientId;
    private String clientSecret;

    public void setClientAuthenticationMethod(String clientAuthenticationMethod) {
        this.clientAuthenticationMethod = new ClientAuthenticationMethod(clientAuthenticationMethod);
    }

    private ClientAuthenticationMethod clientAuthenticationMethod;

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = new AuthorizationGrantType(authorizationGrantType);
    }

    private AuthorizationGrantType authorizationGrantType;
    private String redirectUri;
    private String[] scope;
    private String clientName;
}
