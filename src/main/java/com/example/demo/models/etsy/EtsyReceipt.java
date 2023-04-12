package com.example.demo.models.etsy;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EtsyReceipt {
    private Long receiptId;
    private Long receiptType;
    private Long sellerUserId;
    private String sellerEmail;
    private Long buyerUserId;
    private String buyerEmail;
    private String name;
    private String firstLine;
    private String secondLine;
    private String city;
    private String state;
    private String zip;
    private String status;
    private String formattedAddress;
    private String countryIso;
    private String paymentMethod;
    private String paymentEmail;
    private String messageFromSeller;
    private String messageFromBuyer;
    private String messageFromPayment;
    private Boolean isPaid;
    private Boolean isShipped;
    private Long createTimestamp;
    private Long createdTimestamp;
    private Long updateTimestamp;
    private Long updatedTimestamp;
    private Boolean isGift;
    private String giftMessage;

    // a number equal to the total_price minus
    // the coupon discount plus tax and shipping costs
    private EtsyPrice grandtotal;

    // a number equal to the total_price minus coupon
    // discounts. Does not include tax or shipping costs
    private EtsyPrice subtotal;

    // a number equal to the sum of the individual listings'
    // (price * quantity). Does not include tax or shipping costs
    private EtsyPrice totalPrice;

    private EtsyPrice totalShippingCost;
    private EtsyPrice totalTaxCost;
    private EtsyPrice totalVatCost;
    private EtsyPrice discountAmt;
    private EtsyPrice giftWrapPrice;
    private List<EtsyShipment> shipments;
    private List<EtsyTransaction> transactions;
    private List<Refund> refunds;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Refund {
        private EtsyPrice amount;
        private Long createdTimestamp;
        private String reason;
        private String noteFromIssuer;
        private String status;
    }
}
