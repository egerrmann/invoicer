package com.example.demo.models.etsy.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

//@Getter
//@Setter
//@NoArgsConstructor
//@ToString
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetMeResponse {
    private int userId;
    private int shopId;
}
