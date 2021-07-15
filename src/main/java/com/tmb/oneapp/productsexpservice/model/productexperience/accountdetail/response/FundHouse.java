package com.tmb.oneapp.productsexpservice.model.productexperience.accountdetail.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundHouse {

    private String fundHouseCode;

    private List<Plan> planList;
}
