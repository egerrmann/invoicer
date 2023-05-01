package com.example.demo.configuration.oauth2.etsy;

import com.example.demo.models.etsy.oauth2.OAuthProperties;
import com.example.demo.models.etsy.oauth2.ProviderIdProperties;
import com.example.demo.models.etsy.oauth2.RegistrationIdProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

// TODO: Split this class into two different ones
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class OAuth2LoginConfig {
    private final OAuthProperties properties;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> etsyCustomOAuth2UserService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> moneybirdCustomOAuth2UserService;

    @Bean("etsyFilterChain")
//    @Order(2)
    public SecurityFilterChain etsyFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> {
                            try {
                                authorize
                                        .requestMatchers("/moneybird/**").authenticated()
                                        .and()
                                        // TODO: Make POST methods work without disabling "csrf"
                                        .csrf()
                                        .disable()
                                        .oauth2Login()
                                        .userInfoEndpoint()
                                        .userService(this.moneybirdCustomOAuth2UserService);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                )
                .authorizeHttpRequests(authorize -> {
                        try {
                            authorize
                                    .requestMatchers("/etsy/**", "/invoicer/**").authenticated()
                                    // TODO: May come up with more strict rules for people accessing other URLs
                                    .anyRequest().permitAll();
                                    // next lines are commented because for some reason Etsy's
                                    // auth service is used even during the MB's authorization
                                    // TODO: Uncomment lines and make code work with both auths
//                                    .and()
//                                    .oauth2Login()
                                    // TODO Add refresh token catcher
//                                    .userInfoEndpoint()
//                                    .userService(this.etsyUserService);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                );


        return http.build();
    }

    // TODO: code duplication
    private ClientRegistration etsyClientRegistration() {
        RegistrationIdProperties etsyRegistrationProp = properties.getRegistration().getEtsy();
        ProviderIdProperties moneybirdProviderProp = properties.getProvider().getEtsy();

        return ClientRegistration.withRegistrationId("etsy")
                .clientId(etsyRegistrationProp.getClientId())
                .clientAuthenticationMethod(etsyRegistrationProp.getClientAuthenticationMethod())
                .authorizationGrantType(etsyRegistrationProp.getAuthorizationGrantType())
                .redirectUri(etsyRegistrationProp.getRedirectUri())
                .scope(etsyRegistrationProp.getScope())
                .authorizationUri(moneybirdProviderProp.getAuthorizationUri())
                .tokenUri(moneybirdProviderProp.getTokenUri())
                .build();
    }

    private ClientRegistration moneybirdClientRegistration() {
        RegistrationIdProperties moneybirdRegistrationProp = properties.getRegistration().getMoneybird();
        ProviderIdProperties etsyProviderProp = properties.getProvider().getMoneybird();

        return ClientRegistration.withRegistrationId("moneybird")
                .clientId(moneybirdRegistrationProp.getClientId())
                // TODO: Check if it's secure enough w/o PKCE
                .clientSecret(moneybirdRegistrationProp.getClientSecret())
                .clientAuthenticationMethod(moneybirdRegistrationProp.getClientAuthenticationMethod())
                .authorizationGrantType(moneybirdRegistrationProp.getAuthorizationGrantType())
                .redirectUri(moneybirdRegistrationProp.getRedirectUri())
                .scope(moneybirdRegistrationProp.getScope())
                .authorizationUri(etsyProviderProp.getAuthorizationUri())
                .tokenUri(etsyProviderProp.getTokenUri())
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

    // seems like this bean is not used anywhere
    /*@Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return etsyUserService;
    }*/

    // TODO: Check if auth works without the following beans. Delete if yes
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.etsyClientRegistration(),
                this.moneybirdClientRegistration());
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
