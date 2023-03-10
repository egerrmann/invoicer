package com.example.demo.models.oauth2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EtsyUser {
    private int userId;
    private String primaryEmail;
    private String firstName;
    private String lastName;
    private String imageUrl75x75;

    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_id", this.userId);
        attributes.put("primary_email", this.primaryEmail);
        attributes.put("first_name", this.firstName);
        attributes.put("last_name", this.lastName);
        return attributes;
    }
}
