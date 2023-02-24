package com.example.demo.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CallbackData {
    private String state;
    private String code; // An OAuth authorization code required to request an OAuth token
}
