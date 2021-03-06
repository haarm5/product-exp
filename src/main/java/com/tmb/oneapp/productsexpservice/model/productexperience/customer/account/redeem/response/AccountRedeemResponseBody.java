package com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRedeemResponseBody {

    private String crmId;

    private String accountRedeem;

    private String prefAddressType;
}
