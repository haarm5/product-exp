package com.tmb.oneapp.productsexpservice.model.response.fundpayment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundPaymentDetailResponse {

    private FundRuleInfoList fundRule;

    private List<FundHolidayClassList> fundHolidayList;

    private List<DepositAccount> depositAccountList;
}
