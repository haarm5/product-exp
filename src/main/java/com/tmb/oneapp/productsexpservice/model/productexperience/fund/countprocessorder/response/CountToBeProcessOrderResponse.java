package com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountToBeProcessOrderResponse{
    private CountOrderProcessingResponseBody body;
}