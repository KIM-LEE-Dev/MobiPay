package com.example.mobipay.global.authentication.dto.cardcheck;

import com.example.mobipay.global.authentication.dto.CardRec;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class CardCheckResponse {

    @JsonProperty("Header")
    private Header header;
    @JsonProperty("REC")
    private List<Rec> recs;

    @Getter
    public static class Header {
        private String responseCode;
        private String responseMessage;
        private String apiName;
        private String transmissionDate;
        private String transmissionTime;
        private String institutionCode;
        private String apiKey;
        private String apiServiceCode;
        private String institutionTransactionUniqueNo;
    }

    @Getter
    public static class Rec implements CardRec {
        private String cardNo;
        private String cvc;
        private String cardUniqueNo;
        private String cardIssuerCode;
        private String cardIssuerName;
        private String cardName;
        private String baselinePerformance;
        private String maxBenefitLimit;
        private String cardDescription;
        private String cardExpiryDate;
        private String withdrawalAccountNo;
        private String withdrawalDate;
    }
}