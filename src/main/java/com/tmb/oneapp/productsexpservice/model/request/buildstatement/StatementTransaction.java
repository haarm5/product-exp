package com.tmb.oneapp.productsexpservice.model.request.buildstatement;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StatementTransaction {
    private Integer transactionCode;
    private BigDecimal transactionAmounts;
    private String postedDate;
    private String transactionDate;
    private String mccCode;
    private String transactionDescription;
    private String transactionCurrency;
    private String transactionType;
}
