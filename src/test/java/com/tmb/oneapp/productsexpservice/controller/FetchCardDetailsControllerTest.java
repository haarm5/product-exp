package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardEmail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCreditCardDetailsReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;

@RunWith(JUnit4.class)
public class FetchCardDetailsControllerTest {
    FetchCardDetailsController fetchCardDetailsController;
    @Mock
    CreditCardClient creditCardClient;
    @Mock
    CommonServiceClient commonServiceClient;

    @Mock
    CustomerServiceClient customerServiceClient;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        fetchCardDetailsController = new FetchCardDetailsController(creditCardClient, commonServiceClient, 
        		 customerServiceClient);

    }

    @Test
    void testGetProductConfigSuccess() {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        req.setAccountId("0000000050078360018000167");
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);
        CreditCardDetail creditCardDetail = new CreditCardDetail();
        creditCardDetail.setAccountId("0000000050078360018000167");
        creditCardDetail.setProductId("12345");
        CardEmail cardEmail = new CardEmail();
        cardEmail.setEmaileStatementFlag("");
        creditCardDetail.setCardEmail(cardEmail);
        FetchCardResponse fetchCardResponse = new FetchCardResponse();
        fetchCardResponse.setStatus(silverlakeStatus);
        fetchCardResponse.setCreditCard(creditCardDetail);
        handleGetCardRes(fetchCardResponse, HttpStatus.OK);
        ProductConfig productConfig = new ProductConfig();
        productConfig.setProductNameEN("testen");
        productConfig.setProductNameTH("testth");
        productConfig.setIconId("testicon");
        productConfig.setProductCode("12345");
        List<ProductConfig> listProductConfig = new ArrayList<ProductConfig>();
        listProductConfig.add(productConfig);
        handleProductConfig(listProductConfig, HttpStatus.OK);

        TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
		profile.setEmailAddress("A@B.com");
		profile.setEmailVerifyFlag("Y");
		customerModuleResponse.setData(profile);
		customerModuleResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerProfile(any())).thenReturn(ResponseEntity.ok(customerModuleResponse));
		
        ResponseEntity<TmbOneServiceResponse<FetchCardResponse>> response = fetchCardDetailsController.fetchCardDetails(req,
                reqHeaders);
        assertEquals(200, response.getStatusCodeValue());

    }

    @Test
    void testGetProductConfigNoResData() {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        req.setAccountId("0000000050078360018000167");
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);
        CreditCardDetail creditCardDetail = new CreditCardDetail();
        creditCardDetail.setAccountId("0000000050078360018000167");
        creditCardDetail.setProductId("12345");
        CardEmail cardEmail = new CardEmail();
        cardEmail.setEmaileStatementFlag("");
        creditCardDetail.setCardEmail(cardEmail);
        FetchCardResponse fetchCardResponse = new FetchCardResponse();
        fetchCardResponse.setStatus(silverlakeStatus);
        fetchCardResponse.setCreditCard(creditCardDetail);
        handleGetCardRes(fetchCardResponse, HttpStatus.OK);
        ProductConfig productConfig = new ProductConfig();
        productConfig.setProductNameEN("testen");
        productConfig.setProductNameTH("testth");
        productConfig.setIconId("testicon");
        productConfig.setProductCode("123456");
        List<ProductConfig> listProductConfig = new ArrayList<ProductConfig>();
        listProductConfig.add(productConfig);
        handleProductConfig(listProductConfig, HttpStatus.OK);
        TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
		profile.setEmailAddress("A@B.com");
		profile.setEmailVerifyFlag("Y");
		customerModuleResponse.setData(profile);
		customerModuleResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerProfile(any())).thenReturn(ResponseEntity.ok(customerModuleResponse));
		
        ResponseEntity<TmbOneServiceResponse<FetchCardResponse>> response = fetchCardDetailsController.fetchCardDetails(req,
                reqHeaders);
        assertEquals(200, response.getStatusCodeValue());

    }

    @Test
    void testGetCreditCardDetailsNull() {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        req.setAccountId("0000000050078360018000167");
        handleGetCardRes(null, HttpStatus.OK);
        ResponseEntity<TmbOneServiceResponse<FetchCardResponse>> response = fetchCardDetailsController.fetchCardDetails(req,
                reqHeaders);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    void testGetCreditCardDetailsStatusOne() {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        req.setAccountId("0000000050078360018000167");
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(1);
        FetchCardResponse fetchCardResponse = new FetchCardResponse();
        fetchCardResponse.setStatus(silverlakeStatus);
        handleGetCardRes(fetchCardResponse, HttpStatus.OK);
        ResponseEntity<TmbOneServiceResponse<FetchCardResponse>> response = fetchCardDetailsController.fetchCardDetails(req,
                reqHeaders);
        assertEquals(400, response.getStatusCodeValue());

    }


    @Test
    void testfetchCardDetailsError() throws Exception {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        req.setAccountId("0000000050078360018000167");
        when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenThrow(RuntimeException.class);

        ResponseEntity<TmbOneServiceResponse<FetchCardResponse>> response = fetchCardDetailsController.fetchCardDetails(req,
                reqHeaders);
        assertNull(response.getBody().getData());

    }


    @Test
    void testfetchCardDetailsNoData() throws Exception {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");

        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        req.setAccountId("");
        ResponseEntity<TmbOneServiceResponse<FetchCardResponse>> response = fetchCardDetailsController.fetchCardDetails(req,
                reqHeaders);
        assertEquals(400, response.getStatusCodeValue());

    }

    public void handleGetCardRes(FetchCardResponse fetchCardResponse, HttpStatus status) {
        TmbOneServiceResponse<FetchCardResponse> oneServiceResponse = new TmbOneServiceResponse<FetchCardResponse>();
        oneServiceResponse.setData(fetchCardResponse);
        ResponseEntity<FetchCardResponse> getCardRes = new ResponseEntity<FetchCardResponse>(fetchCardResponse, status);
        when(creditCardClient.getCreditCardDetails(anyString(), anyString())).thenReturn(getCardRes);

    }

    public void handleProductConfig(List<ProductConfig> productConfig, HttpStatus status) {
        TmbOneServiceResponse<List<ProductConfig>> oneServiceResponse = new TmbOneServiceResponse<List<ProductConfig>>();
        oneServiceResponse.setData(productConfig);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = new ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>>(
                oneServiceResponse, status);
        when(commonServiceClient.getProductConfig(anyString())).thenReturn(response);

    }

    public Map<String, String> headerRequestParameter(String reqHeaders) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, reqHeaders);
        return reqData;

    }

}
