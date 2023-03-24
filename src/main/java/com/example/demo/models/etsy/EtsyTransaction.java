package com.example.demo.models.etsy;

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
public class EtsyTransaction {
    private Long transactionId;
    private String title;
    private String description;
    private Long sellerUserId;
    private Long buyerUserId;
    private Long createTimestamp;
    private Long createdTimestamp;
    private Long paidTimestamp;
    private Long shippedTimestamp;
    private Long quantity;
    private Long listingImageId;
    private Long receiptId;
    private Boolean isDigital;
    private String fileData;
    private Long listingId;
    private String transactionType;
    private Long productId;
    private String sku;
    private EtsyPrice price;
    private EtsyPrice shippingCost;
    private List<Variation> variations;
    private List<ProductDaum> productData;
    private Long shippingProfileId;
    private Long minProcessingDays;
    private Long maxProcessingDays;
    private String shippingMethod;
    private String shippingUpgrade;
    private Long expectedShipDate;
    private Long buyerCoupon;
    private Long shopCoupon;

//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @ToString
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Variation {
        private Long propertyId;
        private Long valueId;
        private String formattedName;
        private String formattedValue;
    }

//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @ToString
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ProductDaum {
        private Long propertyId;
        private String propertyName;
        private Long scaleId;
        private String scaleName;
        private List<Long> valueIds;
        private List<String> values;
    }
}
