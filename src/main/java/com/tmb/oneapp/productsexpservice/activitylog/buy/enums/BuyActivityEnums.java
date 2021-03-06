package com.tmb.oneapp.productsexpservice.activitylog.buy.enums;

import lombok.Getter;

@Getter
public enum BuyActivityEnums {

    CLICK_PURCHASE_BUTTON_AT_FUND_FACT_SHEET_SCREEN("101000101", "Buy fund", "Click purchase button on Fund fact Sheet Screen"),
    ENTER_PIN_IS_CORRECT("101000105", "Buy fund", "After enter PIN is correct then display transaction details completed");

    private String activityTypeId;
    private String description;
    private String event;

    BuyActivityEnums(String activityTypeId, String description, String event) {
        this.activityTypeId = activityTypeId;
        this.description = description;
        this.event = event;
    }
}
