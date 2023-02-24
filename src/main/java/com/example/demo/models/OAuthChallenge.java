package com.example.demo.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OAuthChallenge {
    private String randomString;
    private byte[] verifier;
    private String challenge;
    private byte[] challengeBase64;
    private String challengeBase64String;
}
