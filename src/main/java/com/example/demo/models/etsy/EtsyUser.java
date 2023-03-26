package com.example.demo.models.etsy;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EtsyUser {
    private int userId;
    private String primaryEmail;
    private String firstName;
    private String lastName;
    private String imageUrl75x75;
    private String accessToken;

    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_id", this.userId);
        attributes.put("primary_email", this.primaryEmail);
        attributes.put("first_name", this.firstName);
        attributes.put("last_name", this.lastName);
        return attributes;
    }

    // May come up with a way to return and updated  EtsyUser
    // Will need to check the Bean lifecycles again

    // Or actually I will store the data in cookies and make use of them
    // NOTE: may not work for automation

//    public EtsyUser updateUser() {
//        return this;
//    }
}
