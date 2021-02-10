package com.tmb.oneapp.productsexpservice.model.response.accdetail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.response.investment.DetailFund;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class AccDetailBody {
    private OrderToBeProcess orderToBeProcess;
    private DetailFund detailFund;
}
