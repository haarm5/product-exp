package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class ProductConfig {
    private String productCode;
    private String productNameEN;
    private String productNameTH;
    private String iconId;
    private String openEkyc;
    @JsonIgnore
    public String getOpenEKyc() {
        return openEkyc;
    }

    private String allowToPurchaseMf;
    private String accountType;
    private String accountTypeDescTh;
    private String accountTypeDescEn;
    
}
