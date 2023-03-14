package com.example.demo.services;

import com.example.demo.models.EtsyAuthCredentials;
import com.example.demo.models.OAuthChallenge;
import com.example.demo.models.oauth2.EtsyOAuthProperties;
import com.example.demo.services.interfaces.IEtsyAuthService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class EtsyAuthService implements IEtsyAuthService {

    private WebClient client;
    private EtsyOAuthProperties properties;

    public EtsyAuthService(EtsyOAuthProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getOAuthToken() {
        client = WebClient.create();

        OAuthChallenge challengeData = createOAuthChallenge();

        EtsyAuthCredentials credentials = new EtsyAuthCredentials();
        credentials.setCodeChallenge(challengeData.getChallengeBase64String());
        credentials.setClientId(properties.getRegistration().getEtsy().getClientId());
        credentials.setRedirectUri(properties.getRegistration().getEtsy().getRedirectUri());

        HttpHeaders allHeaders = new HttpHeaders();
        allHeaders.add("response_type", credentials.getResponseType());
        allHeaders.add("redirect_uri", credentials.getRedirectUri());
        allHeaders.add("scope", credentials.getScope());
        allHeaders.add("state", credentials.getState());
        allHeaders.add("code_challenge", credentials.getCodeChallenge());
        allHeaders.add("code_challenge_method", credentials.getCodeChallengeMethod());
        allHeaders.add("client_id", credentials.getClientId());

//        CallbackData data;
        String data = "";

        System.out.println("https://www.etsy.com/oauth/connect?" +
                "response_type=" + credentials.getResponseType() +
                "&redirect_uri=" + credentials.getRedirectUri() +
                "&scope=" + credentials.getScope() +
                "&client_id=" + credentials.getClientId() +
                "&state=" + credentials.getState() +
                "&code_challenge=" + credentials.getCodeChallenge() +
                "&code_challenge_method=" + credentials.getCodeChallengeMethod());

        Mono<ResponseEntity<String>> response = client.get()
                .uri("https://www.etsy.com/oauth/connect?" +
                        "response_type=" + credentials.getResponseType() +
                        "&redirect_uri=" + credentials.getRedirectUri() +
                        "&scope=" + credentials.getScope() +
                        "&client_id=" + credentials.getClientId() +
                        "&state=" + credentials.getState() +
                        "&code_challenge=" + credentials.getCodeChallenge() +
                        "&code_challenge_method=" + credentials.getCodeChallengeMethod())
                .retrieve()
                .toEntity(String.class);

        response.subscribe(
                entity -> {
                    System.out.println("\nResponse status: " + entity.getStatusCode() + "\n");
                    System.out.println("\nResponse body: " + entity.getBody() + "\n");
                },
                error -> {
                    System.out.println("\nControllerError: " + error.getMessage());                }
        );

        return data;
    }

    @Override
    public OAuthChallenge createOAuthChallenge() {
        OAuthChallenge challengeData = new OAuthChallenge();

        String randString = generateRandomString();
        // Setting a generated random string
        challengeData.setRandomString(randString);

        // Setting a verifier, which is at the moment is random string bytes
        challengeData.setVerifier(randString.getBytes());

        // Creating a challenge by hashing a verifier
        String challenge = Sha2Crypt.sha256Crypt(challengeData.getVerifier().clone());
        challengeData.setChallenge(challenge);

        // Getting Base64 bytes of the challenge
        byte[] challengeBase64 = Base64.encodeBase64(challenge.getBytes());
        challengeData.setChallengeBase64(challengeBase64);

        // Getting Base64 bytes of the challenge
        String challengeBase64String = Base64.encodeBase64URLSafeString(challenge.getBytes());
        challengeData.setChallengeBase64String(challengeBase64String);

        System.out.println("Random string: " + challengeData.getRandomString());
        System.out.println("Verifier: " + Arrays.toString(challengeData.getVerifier()));
        System.out.println("Challenge: " + challengeData.getChallenge());
        System.out.println("Challenge bytes: " + Arrays.toString(challengeData.getChallenge().getBytes()));
        System.out.println("Challenge Base64: " + Arrays.toString(challengeData.getChallengeBase64()));
        System.out.println("Challenge Base64 but as a url-safe String: " + challengeData.getChallengeBase64String());

        return challengeData;
    }

    private String generateRandomString() {
        Character[] chars = getCharsForRandomString();

        // Firstly, we define how many characters will be in the string
        Random rand1 = new Random();
        int length = rand1.nextInt(127 - 44) + 44;
        // It is done to follow Etsy's guidelines that say that the length should be "between 43 and 128"
        // It is assumed that Etsy means that 43 and 128 are not included.

        // Generating the chars and adding to the string
        String randString = "";

        for (int i = 0; i < length; i++) {
            Random rand2 = new Random();
            char symbol = chars[rand2.nextInt(chars.length)];
            randString += symbol;
        }

        return randString;
    }

    private Character[] getCharsForRandomString() {
        ArrayList<Character> chars = new ArrayList<>();
        // Adding numbers and other needed characters
        chars.addAll(List.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.', '-', '_', '~'));

        // Adding all the letters
        for (int i = 65; i < 91; i++) {
            // Adds a capital letter
            chars.add( (char) i);
            // Adds a small letter
            chars.add( (char) (i + 32));
        }
        return chars.toArray(new Character[0]);
    }
}
