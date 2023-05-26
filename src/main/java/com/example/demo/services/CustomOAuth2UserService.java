package com.example.demo.services;

import com.example.demo.models.etsy.EtsyShop;
import com.example.demo.models.etsy.oauth2.AccessTokenReceivedEvent;
import com.example.demo.models.etsy.oauth2.EtsyOAuthProperties;
import com.example.demo.models.etsy.EtsyUser;
import com.example.demo.models.etsy.responses.GetMeResponse;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Value;
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

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    // This class intercepts Spring's oauth2 login and does it in a custom way.
    // More in detail, it gets the information about the User and their Shop,
    // logs in the user and keeps the information of both the user and the shop

    private EtsyOAuthProperties properties;
    private ApplicationEventPublisher publisher;

    @Value("${etsy.base-url}")
    private String baseUrl;

    public CustomOAuth2UserService(EtsyOAuthProperties properties, ApplicationEventPublisher publisher) {
        this.properties = properties;
        this.publisher = publisher;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("authority"));
        String accessToken = oAuth2UserRequest.getAccessToken().getTokenValue();
        WebClient webClient = setWebClient(accessToken);
        Pair<EtsyUser, EtsyShop> userAndShop= getUserAndShop(webClient, accessToken);

        final OAuth2User user = new DefaultOAuth2User(authorities, userAndShop.getValue0().getAttributes(), "first_name");
        return user;
    }

    // Sets needed headers for the WebClient
    private WebClient setWebClient(String accessToken) {
        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader("x-api-key", properties.getRegistration().getEtsy().getClientId())
                .build();

        return webClient;
    }

    // Gets the information about the User and the Shop
    private Pair<EtsyUser, EtsyShop> getUserAndShop(WebClient webClient, String accessToken) {
        GetMeResponse ids = getIds(webClient);
        EtsyUser user = getUser(webClient, ids.getUserId(), accessToken);
        EtsyShop shop = getShop(webClient, ids.getShopId());

        Pair<EtsyUser, EtsyShop> userAndShop = new Pair<>(user, shop);

        // This method invokes an event that transfers the User and Shop data
        // to the EtsyService
        publisher.publishEvent(new AccessTokenReceivedEvent(userAndShop));

        return userAndShop;
    }

    // TODO Remove subscribe() method because mono is blocked twice here
    // Gets ids of the User and the shop they own
    private GetMeResponse getIds(WebClient webClient) {
        Mono<GetMeResponse> resp = webClient.get()
                .uri(baseUrl + "users/me")
                .retrieve()
                .bodyToMono(GetMeResponse.class);
        resp.subscribe(System.out::println, error -> {
            System.out.println(error.toString());
        });

        return resp.block();
    }

    // Gets the User information
    private EtsyUser getUser(WebClient webClient, int userId, String accessToken) {
        Mono<EtsyUser> resp = webClient.get()
                .uri(baseUrl + "users/" + userId)
                .retrieve()
                .bodyToMono(EtsyUser.class);
        EtsyUser user = resp.block();

        assert user != null;
        user.setAccessToken(accessToken);

        return user;
    }

    // Gets the Shop information
    private EtsyShop getShop(WebClient webClient, int shopId) {
        Mono<EtsyShop> resp = webClient.get()
                .uri(baseUrl + "shops/" + shopId)
                .retrieve()
                .bodyToMono(EtsyShop.class);
        EtsyShop shop = resp.block();

        return shop;
    }
}