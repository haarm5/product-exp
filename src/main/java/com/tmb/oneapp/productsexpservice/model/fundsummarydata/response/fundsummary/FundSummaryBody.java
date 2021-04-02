package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.PortfolioByPort;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundSummaryBody extends FundClassList{
    private FundClassList fundClassList;
    private String feeAsOfDate;
    private BigDecimal sumAccruedFee;
    private PercentOfFundType percentOfFundType;
    private List<String> portsUnitHolder;
    private List<FundSearch> searchList;
    private List<PortfolioByPort> summaryByPort;

}
