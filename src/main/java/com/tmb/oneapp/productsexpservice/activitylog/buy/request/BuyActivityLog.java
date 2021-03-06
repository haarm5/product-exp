package com.tmb.oneapp.productsexpservice.activitylog.buy.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyActivityLog extends BaseEvent {

    @JsonProperty("unit_holder")
    private String unitHolder;

    @JsonProperty("fund_name")
    private String fundName;

    @JsonProperty("verify_flag")
    private String verifyFlag;

    private String reason;

    @JsonProperty("fund_class")
    private String fundClass;

    @JsonProperty("activity_type")
    private String activityType;

    /* Enter Pin Is Correct */
    private String status;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("from_bank_account")
    private String fromBankAccount;

    public BuyActivityLog(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}
