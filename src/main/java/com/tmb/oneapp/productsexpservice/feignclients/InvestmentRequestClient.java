package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListPage;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * InvestmentClient interface consume account details from investment service
 */
@FeignClient(name = "${investment.service.name}", url = "${investment.service.url}")
public interface InvestmentRequestClient {

    /**
     * Call investment fund rule service response entity.
     *
     * @param headers             callAccountService method consume account details from                    core banking
     * @param fundRuleRequestBody the fund rule request body
     * @return response entity
     */
    @PostMapping(value = "${investment.service.fundrule.url}")
    @ResponseBody
    public ResponseEntity<TmbOneServiceResponse<FundRuleBody>> callInvestmentFundRuleService(@RequestHeader Map<String, String> headers, @RequestBody FundRuleRequestBody fundRuleRequestBody);

    /**
     * Call investment fund acc detail service response entity.
     *
     * @param headers       callAccountService method consume account details from                    core banking
     * @param fundAccountRq the fund account rq
     * @return response entity
     */
    @PostMapping(value = "${investment.service.accountdetail.url}")
    @ResponseBody
    public ResponseEntity<TmbOneServiceResponse<AccDetailBody>> callInvestmentFundAccDetailService(@RequestHeader Map<String, String> headers, @RequestBody FundAccountRequestBody fundAccountRq);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers    the headers
     * @param unitHolder the unit holder
     * @return the fund summary response
     */
    @PostMapping(value = "${investment.service.fund.summary.url}")
    @ResponseBody
    public ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> callInvestmentFundSummaryService(@RequestHeader Map<String, String> headers
            , @RequestBody UnitHolder unitHolder);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers  the headers
     * @param fundCode the fund code
     * @return the fund summary response
     */
    @GetMapping(value = "${investment.service.fund.holiday.url}")
    public ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> callInvestmentFundHolidayService(@RequestHeader Map<String, String> headers, @PathVariable("fundCode") String fundCode);


    /**
     * Call investment fund listInfo service fund summary response.
     *
     * @param headers  the headers
     * @return the fund summary response
     */
    @PostMapping(value = "${investment.service.fund.listinfo.url}")
    @ResponseBody
    public ResponseEntity<TmbOneServiceResponse<FundListPage>> callInvestmentFundListInfoService(@RequestHeader Map<String, Object> headers);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers    the headers
     * @param ffsRequestBody the unit holder
     * @return the fund fact sheet response
     */
    @PostMapping(value = "${investment.service.fund.factsheet.url}")
    @ResponseBody
    public ResponseEntity<TmbOneServiceResponse<FfsResponse>> callInvestmentFundFactSheetService(@RequestHeader Map<String, String> headers
            , @RequestBody FfsRequestBody ffsRequestBody);

}
