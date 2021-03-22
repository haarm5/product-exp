package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "account_id",
        "card_installment"
})
@AllArgsConstructor
public class CreditCardInstallment {

        @JsonProperty("account_id")
        private String accountId;
        @JsonProperty("card_installment")
        private CardInstallment cardInstallment;

    }