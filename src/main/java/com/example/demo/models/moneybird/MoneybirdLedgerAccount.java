package com.example.demo.models.moneybird;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoneybirdLedgerAccount {
    private String id;
    private BigInteger administrationId;
    private String name;
    private String accountType;
    private String accountId;
    private String parentId;
    private String createdAt;
    private String updatedAt;
    private List<String> allowedDocumentTypes;
}
