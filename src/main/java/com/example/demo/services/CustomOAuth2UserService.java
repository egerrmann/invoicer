package com.example.demo.services;

import com.example.demo.models.AccessTokenReceivedEvent;
import com.example.demo.models.oauth2.EtsyOAuthProperties;
import com.example.demo.models.oauth2.EtsyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // Apparently, this class is working, but I get an error
    // So I need to figure out what causes the error and get rid of it

    private EtsyOAuthProperties properties;
    private ApplicationEventPublisher publisher;
    private EtsyUser user;

    public CustomOAuth2UserService(EtsyOAuthProperties properties, ApplicationEventPublisher publisher, EtsyUser user) {
        this.properties = properties;
        this.publisher = publisher;
        this.user = user;
    }

    private WebClient setWebClient(String accessToken) {
        WebClient webClient = WebClient.builder()
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader("x-api-key", properties.getRegistration().getEtsy().getClientId())
                .build();

        return webClient;
    }

    private int getUserId(String accessToken) {
        String idAsString = accessToken.substring(0, accessToken.indexOf("."));
        return Integer.parseInt(idAsString);
    }

    private EtsyUser getUser(String accessToken) {
        WebClient webClient = setWebClient(accessToken);
        int userId = getUserId(accessToken);
        Mono<EtsyUser> resp = webClient.get()
                .uri("https://openapi.etsy.com/v3/application/users/" + userId)
                .retrieve()
                .bodyToMono(EtsyUser.class);
        resp.subscribe(System.out::println, error -> {
            System.out.println(error.toString());
        });
        user = resp.block();

        assert user != null;
        user.setAccessToken(accessToken);

        publisher.publishEvent(new AccessTokenReceivedEvent(user));
        return user;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("authority"));
        EtsyUser etsyUser = getUser(oAuth2UserRequest.getAccessToken().getTokenValue());
//        getUser(oAuth2UserRequest.getAccessToken().getTokenValue());
//        EtsyUser etsyUser = new EtsyUser();
        final OAuth2User user = new DefaultOAuth2User(authorities, etsyUser.getAttributes(), "first_name");
        return user;
    }
}