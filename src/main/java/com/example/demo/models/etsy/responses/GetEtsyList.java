package com.example.demo.models.etsy.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

//@Getter
//@Setter
//@NoArgsConstructor
//@ToString
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetEtsyList {
    private int count;
    private List<?> results;
}
