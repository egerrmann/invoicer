package com.example.demo.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Setter
@Getter
@NoArgsConstructor
public class EtsyAuthCredentials {
    private String responseType = "code";

//    @Value("${etsyClientId}")
    private String clientId;

//    @Value("${callbackURL}")
    private String redirectUri;

    private String scope = "billing_r";
    private String state = "superstate";
    private String codeChallenge = "";
    private String codeChallengeMethod = "S256";
}
