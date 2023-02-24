package com.example.demo.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EtsyOAuth {
    private String state;
    private String code; // An OAuth authorization code required to request an OAuth token
}
