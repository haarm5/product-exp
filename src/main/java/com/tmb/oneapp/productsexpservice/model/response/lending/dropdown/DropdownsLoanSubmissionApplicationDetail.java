package com.tmb.oneapp.productsexpservice.model.response.lending.dropdown;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DropdownsLoanSubmissionApplicationDetail {
    private List<Dropdowns.PaymentCriteria> paymentCriteria;
}
