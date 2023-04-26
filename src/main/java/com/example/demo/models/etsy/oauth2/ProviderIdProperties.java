package com.example.demo.models.etsy.oauth2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderIdProperties {
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
}
