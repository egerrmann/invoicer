package com.example.demo.models.etsy;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EtsyLedger {
    private Long entryId;
    private Long ledgerId;
    private Long sequenceNumber;
    private Long amount;
    private String currency;
    private String description;
    private Long balance;
    private Long createDate;
    private Long createdTimestamp;
    private String ledgerType;
    private String referenceType;
    private String referenceId;
    private List<PaymentAdjustment> paymentAdjustments;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PaymentAdjustment {
        private Long paymentAdjustmentId;
        private Long paymentId;
        private String status;
        private Boolean isSuccess;
        private Long userId;
        private String reasonCode;
        private Long totalAdjustmentAmount;
        private Long shopTotalAdjustmentAmount;
        private Long buyerTotalAdjustmentAmount;
        private Long totalFeeAdjustmentAmount;
        private Long createTimestamp;
        private Long createdTimestamp;
        private Long updateTimestamp;
        private Long updatedTimestamp;
        private List<PaymentAdjustmentItem> paymentAdjustmentItems;

        @Data
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class PaymentAdjustmentItem {
            private Long paymentAdjustmentId;
            private Long paymentAdjustmentItemId;
            private String adjustmentType;
            private Long amount;
            private Long shopAmount;
            private Long transactionId;
            private Long billPaymentId;
            private Long createdTimestamp;
            private Long updatedTimestamp;
        }
    }
}
