package com.example.demo.configuration.moneybird;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
//@EnableWebSecurity
public class WebClientConfigMB {
    @Value("${moneybird.bearer-token}")
    private String token;
    @Value("${moneybird.base-url}")
    private String baseUrl;

    @Bean
    public WebClient webClientWithBaseUrl() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }


    // TODO: Learn why two SecurityFilterChain classes don't work together
    // However, we could just create another Config class,
    // where we have a single SecurityFilterChain with all the rules
//    @Bean("mbFilterChain")
////    @Order(1)
//    public SecurityFilterChain moneybirdFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/moneybird/**").permitAll()
//                        // TODO: May come up with more strict rules for people accessing other URLs
//                )
//                .csrf()
//                .disable();
////                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
//
//        return http.build();
//    }
}
