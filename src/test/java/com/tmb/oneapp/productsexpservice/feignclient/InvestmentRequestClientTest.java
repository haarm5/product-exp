package com.tmb.oneapp.productsexpservice.feignclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class InvestmentRequestClientTest {

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private AccountDetailResponse accountDetailResponse = null;

    private FundListBySuitScoreBody fundListBySuitScoreBody = null;

    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private Map<String, String> createHeader(String correlationId) {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }

    @Test
    public void testGetFundAccountDetailInvestment() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        ResponseEntity<TmbOneServiceResponse<AccountDetailResponse>> responseEntity;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setPortfolioNumber("PT000000001");
        TmbOneServiceResponse<AccountDetailResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            accountDetailResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccountDetailResponse.class);

            oneServiceResponse.setData(accountDetailResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccountDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseEntity = investmentRequestClient.callInvestmentFundAccountDetailService(createHeader(corrID), fundAccountRq);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        Assert.assertEquals(accountDetailResponse, responseEntity.getBody().getData());
        Assert.assertNotNull(responseEntity.getBody().getData().getFundDetail());
    }

    @Test
    public void testGetFundAccountDetailInvestmentNull() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        ResponseEntity<TmbOneServiceResponse<AccountDetailResponse>> responseEntity;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setPortfolioNumber("PT000000001");
        TmbOneServiceResponse<AccountDetailResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            accountDetailResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccountDetailResponse.class);

            oneServiceResponse.setData(accountDetailResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccountDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseEntity = investmentRequestClient.callInvestmentFundAccountDetailService(createHeader(corrID), fundAccountRq);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCodeValue());
    }

    /* ---------  Test filtered fund List based on suitScore ---------  */
    @Test
    public void testCallListFundInfo() {
        ResponseEntity<TmbOneServiceResponse<FundListBySuitScoreBody>> responseEntity;
        TmbOneServiceResponse<FundListBySuitScoreBody> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundListBySuitScoreBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_filter.json").toFile(), FundListBySuitScoreBody.class);

            oneServiceResponse.setData(fundListBySuitScoreBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentListFundInfoService(createHeader(corrID)))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseEntity = investmentRequestClient.callInvestmentListFundInfoService(createHeader(corrID));
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        Assert.assertNotNull(responseEntity.getBody().getData().getFundClassList());
    }

    /* ---------  Test filtered fund List Not Found based on suitScore ---------  */
    @Test
    public void testCallListFundInfoNotFound() {
        ResponseEntity<TmbOneServiceResponse<FundListBySuitScoreBody>> responseEntity;
        TmbOneServiceResponse<FundListBySuitScoreBody> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundListBySuitScoreBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_filter_not_fund.json").toFile(), FundListBySuitScoreBody.class);

            oneServiceResponse.setData(fundListBySuitScoreBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                    ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));

            when(investmentRequestClient.callInvestmentListFundInfoService(createHeader(corrID)))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseEntity = investmentRequestClient.callInvestmentListFundInfoService(createHeader(corrID));
        Assert.assertEquals(responseEntity.getBody().getData().getFundClassList(), oneServiceResponse.getData().getFundClassList());
    }
}