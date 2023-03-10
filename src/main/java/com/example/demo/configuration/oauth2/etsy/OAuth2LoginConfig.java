package com.example.demo.configuration.oauth2.etsy;

import com.example.demo.models.oauth2.EtsyOAuthProperties;
import com.example.demo.services.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class OAuth2LoginConfig {

    private final EtsyOAuthProperties properties;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

    public OAuth2LoginConfig(EtsyOAuthProperties properties, CustomOAuth2UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(withDefaults())
//                .oauth2Client(withDefaults())
//                .headers()
//                .addHeaderWriter(new StaticHeadersWriter("x-api-key", properties.getRegistration().getEtsy().getClientId()))
//                .addHeaderWriter(new StaticHeadersWriter(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2Login()
//                .clientRegistrationRepository(this.clientRegistrationRepository())
//                .authorizedClientService(this.authorizedClientService())
//                .loginPage("/login")
                .userInfoEndpoint()
                .userService((OAuth2UserService<OAuth2UserRequest, OAuth2User>) this.userService);

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
                .userInfoUri(properties.getProvider().getEtsy().getUserInfoUri())
                // may try to check if any other IdTokenClaimsNames may work
                .userNameAttributeName(IdTokenClaimNames.SUB)
//                // when using either QUERY or HEAD it throws a different error
//                // check why, mb the other error is more "correct" for my case
//                .userInfoAuthenticationMethod(AuthenticationMethod.FORM)
                .clientName(properties.getRegistration().getEtsy().getClientName())
                .build();
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
