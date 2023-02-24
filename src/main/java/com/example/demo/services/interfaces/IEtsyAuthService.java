package com.example.demo.services.interfaces;

import com.example.demo.models.EtsyOAuth;
import com.example.demo.models.OAuthChallenge;

public interface IEtsyAuthService {
    String getOAuthToken();
    OAuthChallenge createOAuthChallenge();
}
