package com.example.demo.models.moneybird;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashMap;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoneybirdAdministration {
    private BigInteger id;
    private String name;

    public HashMap<String, Object> getAttributes() {
        return new HashMap<String, Object>() {{
            put("id", id);
            put("name", name);
        }};
    }
}
