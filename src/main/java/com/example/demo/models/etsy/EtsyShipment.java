package com.example.demo.models.etsy;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EtsyShipment {
    private Long receiptShippingId;
    private Long shipmentNotificationTimestamp;
    private String carrierName;
    private String trackingCode;
}
