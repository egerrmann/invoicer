package com.example.demo.configuration.oauth2.etsy;

import com.example.demo.models.etsy.oauth2.EtsyOAuthProperties;
import com.example.demo.services.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class OAuth2LoginConfig {

    private final EtsyOAuthProperties properties;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

    public OAuth2LoginConfig(EtsyOAuthProperties properties, CustomOAuth2UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    @Bean("etsyFilterChain")
//    @Order(2)
    public SecurityFilterChain etsyFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> {
                            try {
                                authorize
                                        .requestMatchers("/moneybird/**")
                                        .permitAll()
                                        .and()
                                        // TODO: Make POST methods work without disabling "csrf"
                                        .csrf()
                                        .disable();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                )
                .authorizeHttpRequests(authorize -> {
                        try {
                            authorize
                                    .requestMatchers("/etsy/**").authenticated()
                                    // TODO: May come up with more strict rules for people accessing other URLs
                                    .anyRequest().permitAll()
                                    .and()
                                    .oauth2Login()
                                    // TODO Add refresh token catcher
                                    .userInfoEndpoint()
                                    .userService(this.userService);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                );


        return http.build();
    }

    private ClientRegistration etsyClientRegistration() {
        return ClientRegistration.withRegistrationId("etsy")
                .clientId(properties.getRegistration().getEtsy().getClientId())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(properties.getRegistration().getEtsy().getRedirectUri())
                .scope(properties.getRegistration().getEtsy().getScope())
                .authorizationUri(properties.getProvider().getEtsy().getAuthorizationUri())
                .tokenUri(properties.getProvider().getEtsy().getTokenUri())
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .build();
        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
		return userService;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.etsyClientRegistration());
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(
            OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }
}
