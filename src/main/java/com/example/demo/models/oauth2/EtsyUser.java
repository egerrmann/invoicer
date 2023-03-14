package com.example.demo.models.oauth2;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EtsyUser {
    private int userId;
    private String primaryEmail;
    private String firstName;
    private String lastName;
    private String imageUrl75X75;
    private String accessToken;

    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_id", this.userId);
        attributes.put("primary_email", this.primaryEmail);
        attributes.put("first_name", this.firstName);
        attributes.put("last_name", this.lastName);
        return attributes;
    }
}
