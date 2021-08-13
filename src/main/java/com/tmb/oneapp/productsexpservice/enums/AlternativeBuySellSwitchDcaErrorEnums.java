package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum AlternativeBuySellSwitchDcaErrorEnums {

    NOT_IN_SERVICE_HOUR("2000001","Customer trade not in service available", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    AGE_NOT_OVER_TWENTY("2000042","Age is not over 20 years old", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    NO_ACTIVE_CASA_ACCOUNT("2000019","No active CASA account", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CUSTOMER_IN_LEVEL_C3_AND_B3(AlternativeBuySellSwitchDcaErrorEnums.ERROR_CODE_200018,"Customer is in risk level C3 ,B3", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CUSTOMER_IDENTIFY_ASSURANCE_LEVEL(AlternativeBuySellSwitchDcaErrorEnums.ERROR_CODE_200018,"If IAL level >= 210 and IAL <> Null then allow", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CUSTOMER_IN_RESTRICTED_LIST(AlternativeBuySellSwitchDcaErrorEnums.ERROR_CODE_200018,"Customer is in restricted list", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CUSTOMER_NOT_FILL_FATCA_FORM("2000032","Customer has not filled in the FATCA form", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CUSTOMER_SUIT_EXIRED("2000004","Customer Suitability Expired", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC);

    private static final String ERROR_DESC = "error";
    private static final String ERROR_CODE_200018 = "2000018";
    private String code;
    private String msg;
    private String desc;

    AlternativeBuySellSwitchDcaErrorEnums(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }
}