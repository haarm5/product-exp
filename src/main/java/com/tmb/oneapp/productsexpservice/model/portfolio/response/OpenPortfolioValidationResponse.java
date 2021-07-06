package com.tmb.oneapp.productsexpservice.model.portfolio.response;

import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioValidationResponse {

    private AccountPurposeResponseBody accountPurposeResponse;

    private DepositAccount depositAccount;
}
