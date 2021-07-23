package com.tmb.oneapp.productsexpservice.service;


import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreBody;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FundFilterService class will get fund Details from MF Service
 */
@Service
public class FundFilterService {


    private static final TMBLogger<FundFilterService> logger = new TMBLogger<>(FundFilterService.class);
    private final InvestmentRequestClient investmentRequestClient;
    /**
     * Instantiates a new Fund Filter Controller.
     * @param investmentRequestClient the investment Request Client
     */
    @Autowired
    public FundFilterService(InvestmentRequestClient investmentRequestClient) {

        this.investmentRequestClient = investmentRequestClient;
    }
    /**
     * Get Fund List By SuitScore Body Response
     *
     * @param correlationId the correlation id
     * @param rq            the rq
     * @return the  response
     */

    public FundListBySuitScoreBody getFundListBySuitScore(String correlationId,FundListBySuitScoreRequest rq )
    {
        FundListBySuitScoreBody response = new FundListBySuitScoreBody();
        Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
        try {
            String suitScore = rq.getSuitScore();
            ResponseEntity<TmbOneServiceResponse<FundListBySuitScoreBody>> fundListBySuitScoreBodyResponse =
                    investmentRequestClient.callInvestmentListFundInfoService(invHeaderReqParameter);

            List<FundClassListInfo> fundList = fundListBySuitScoreBodyResponse.getBody().getData().getFundClassList();
            return filterFundListBasedOnSuitScore(fundList,suitScore);
        } catch (Exception ex) {
            logger.info("error : {}", ex);
            response.setFundClassList(null);
            return response;
        }
    }
    /**
     * Get Filtered Fund List By SuitScore Body Response
     *
     * @param fundList the fund List
     * @param  suitScore        the suitScore
     * @return the fund List By SuitScore Body Responses
     */
    private FundListBySuitScoreBody filterFundListBasedOnSuitScore(List<FundClassListInfo> fundList, String suitScore) {
        FundListBySuitScoreBody fundListBySuitScoreBodyResponses = new FundListBySuitScoreBody();
        fundListBySuitScoreBodyResponses.setFundClassList(fundList.stream().filter(t -> t.getRiskRate().equals(suitScore))
                .collect(Collectors.toList()));
        return fundListBySuitScoreBodyResponses;
    }
}
