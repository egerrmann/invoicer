package com.example.demo.services;

import com.example.demo.configuration.moneybird.WebClientConfig;
import com.example.demo.models.moneybird.MoneybirdAdministration;
import com.example.demo.services.interfaces.IMoneybirdAdministrationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class MoneybirdCustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private IMoneybirdAdministrationService administrationService;
    private ApplicationContext applicationContext;
    private WebClientConfig webClientConfig;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String accessToken = userRequest.getAccessToken().getTokenValue();
//        System.out.println(accessToken);
        MoneybirdAdministration administration = getAdministration(accessToken);

        updateWebclientBean(accessToken, administration.getId().toString());

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("authority"));
        return new DefaultOAuth2User(authorities, administration.getAttributes(), "name");
    }

    private MoneybirdAdministration getAdministration(String accessToken) {
        List<MoneybirdAdministration> administrations = administrationService
                .getAdministrations(accessToken)
                .collectList()
                .block();

        // TODO: Figure out if moneybird provides only one administration
        if (administrations.size() > 1)
            throw new OAuth2AuthenticationException("Failed to get one administration, " +
                    "several of them are provided");

        return administrations.get(0);
    }

    private void updateWebclientBean(String accessToken, String administrationId) {
        DefaultSingletonBeanRegistry registry =
                (DefaultSingletonBeanRegistry) applicationContext
                        .getAutowireCapableBeanFactory();
        registry.destroySingleton("webClientWithBaseUrl");
        webClientConfig.updateBaseUrl(administrationId);
        registry.registerSingleton("webClientWithBaseUrl",
                webClientConfig.webClientWithBaseUrl(accessToken));
    }
}
