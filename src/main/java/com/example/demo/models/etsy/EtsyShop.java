package com.example.demo.models.etsy;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EtsyShop {
    private Long shopId;
    private Long userId;
    private String shopName;
    private Long createDate;
    private Long createdTimestamp;
    private String title;
    private String announcement;
    private String currencyCode;
    private Boolean isVacation;
    private String vacationMessage;
    private String saleMessage;
    private String digitalSaleMessage;
    private Long updateDate;
    private Long updatedTimestamp;
    private Long listingActiveCount;
    private Long digitalListingCount;
    private String loginName;
    private Boolean acceptsCustomRequests;
    private String policyWelcome;
    private String policyPayment;
    private String policyShipping;
    private String policyRefunds;
    private String policyAdditional;
    private String policySellerInfo;
    private Long policyUpdateDate;
    private Boolean policyHasPrivateReceiptInfo;
    private Boolean hasUnstructuredPolicies;
    private String policyPrivacy;
    private String vacationAutoreply;
    private String url;
    private String imageUrl760x100;
    private Long numFavorers;
    private List<String> languages;
    private String iconUrlFullxfull;
    private Boolean isUsingStructuredPolicies;
    private Boolean hasOnboardedStructuredPolicies;
    private Boolean includeDisputeFormLink;
    private Boolean isDirectCheckoutOnboarded;
    private Boolean isEtsyPaymentsOnboarded;
    private Boolean isCalculatedEligible;
    private Boolean isOptedInToBuyerPromise;
    private Boolean isShopUsBased;
    private Long transactionSoldCount;
    private String shippingFromCountryIso;
    private String shopLocationCountryIso;
    private Long reviewCount;
    private Long reviewAverage;
}
