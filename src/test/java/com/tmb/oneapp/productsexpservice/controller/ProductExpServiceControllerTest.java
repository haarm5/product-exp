package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.fundallocation.SuggestAllocationDTO;
import com.tmb.oneapp.productsexpservice.model.fundallocation.request.SuggestAllocationBodyRequest;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.alternative.AlternativeRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRq;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ProductExpServiceControllerTest {

    @Mock
    private ProductsExpService productsExpService;

    @InjectMocks
    private ProductExpServiceController productExpServiceController;

    private final String success_code = "0000";

    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private AccountDetailBody accountDetailBody = null;

    private FundRuleBody fundRuleBody = null;

    private FundAccountResponse fundAccountResponse = null;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private FundAccountDetail mappingResponse(AccountDetailBody accountDetailBody, FundRuleBody fundRuleBody) {
        FundRule fundRule = new FundRule();
        List<FundRuleInfoList> fundRuleInfoList = null;
        AccountDetail accountDetail = new AccountDetail();
        FundAccountDetail fundAccountDetail = new FundAccountDetail();
        if (!StringUtils.isEmpty(fundRuleBody)) {
            fundRuleInfoList = fundRuleBody.getFundRuleInfoList();
            FundRuleInfoList ruleInfoList = fundRuleInfoList.get(0);
            BeanUtils.copyProperties(ruleInfoList, fundRule);
            fundRule.setIpoflag(ruleInfoList.getIpoflag());
            BeanUtils.copyProperties(accountDetail, accountDetailBody.getFundDetail());
            fundAccountDetail.setFundRuleInfoList(fundRuleInfoList);
            fundAccountDetail.setAccountDetail(accountDetail);
        }
        return fundAccountDetail;
    }

    private void initAccountDetailBody() {
        accountDetailBody = new AccountDetailBody();
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("TTTTT");
        fundDetail.setFundHouseCode("EEEEE");
        accountDetailBody.setFundDetail(fundDetail);
    }

    private void initFundRuleBody() {
        fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleInfoList = new ArrayList<>();
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundCode("TTTTTT");
        list.setProcessFlag("N");
        fundRuleInfoList.add(list);
        fundRuleBody.setFundRuleInfoList(fundRuleInfoList);
    }

    private void initSuccessResponseAccountDetail() {
        fundAccountResponse = new FundAccountResponse();
        FundAccountDetail details = new FundAccountDetail();
        FundRule fundRule = new FundRule();
        fundRule.setIpoflag("N");
        fundRule.setFundAllowOtx("Y");
        fundRule.setFundCode("TESEQDSSFX");
        fundRule.setRiskRate("1");
        fundRule.setFundHouseCode("TFUND");
        fundRule.setOrderType("1");
        fundRule.setAllotType("3");
        fundRule.setAllowAipFlag("Y");
        fundRule.setDateAfterIpo("20200413");
        fundRule.setFrontEndFee("0");
        details.setFundRuleInfoList(null);

        AccountDetail accountDetail = new AccountDetail();
        List<FundOrderHistory> ordersHistories = new ArrayList<>();
        FundOrderHistory fundOrderHistory = new FundOrderHistory();
        fundOrderHistory.setAmount("2000.00");
        fundOrderHistory.setOrderDate("20200413");

        FundOrderHistory fundOrderHistoryOne = new FundOrderHistory();
        fundOrderHistoryOne.setAmount("2000.00");
        fundOrderHistoryOne.setOrderDate("20200413");

        ordersHistories.add(fundOrderHistory);
        ordersHistories.add(fundOrderHistoryOne);
        accountDetail.setOrdersHistories(ordersHistories);

        details.setAccountDetail(accountDetail);
        fundAccountResponse.setDetails(details);
    }

    @Test
    public void testGetFundAccountDetailFullReturn() {
        initSuccessResponseAccountDetail();
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");

        try {
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(fundAccountResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(success_code, actualResult.getBody().getStatus().getCode());

        FundAccountResponse newResponse = actualResult.getBody().getData();
        assertEquals(fundAccountResponse, newResponse);
        assertEquals(fundAccountResponse.getDetails().getAccountDetail().getOrdersHistories().size(),
                newResponse.getDetails().getAccountDetail().getOrdersHistories().size());
    }

    @Test
    public void testGetFundAccountDetailNotFound() {
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");

        try {
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void testGetFundAccountDetail() {
        initAccountDetailBody();
        initFundRuleBody();

        FundAccountResponse fundAccountResponse = null;
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");

        AccountDetailBody accountDetailBody = null;
        FundRuleBody fundRuleBody = null;

        try {
            fundAccountResponse = new FundAccountResponse();

            FundAccountDetail fundAccountDetail = mappingResponse(accountDetailBody, fundRuleBody);
            fundAccountResponse.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(fundAccountResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getDetails());
    }

    @Test
    public void testGetFundPrePaymentDetailNotNull() {
        FundPaymentDetailRq fundPaymentDetailRq = new FundPaymentDetailRq();
        fundPaymentDetailRq.setCrmId("001100000000000000000012025950");
        fundPaymentDetailRq.setFundCode("SCBTMF");
        fundPaymentDetailRq.setFundHouseCode("SCBAM");
        fundPaymentDetailRq.setTranType("1");

        FundPaymentDetailRs fundPaymentDetailRs;

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundPaymentDetailRs = mapper.readValue(Paths.get("src/test/resources/investment/fund_payment_detail.json").toFile(), FundPaymentDetailRs.class);
            when(productsExpService.getFundPrePaymentDetail(corrID, fundPaymentDetailRq)).thenReturn(fundPaymentDetailRs);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> actualResult = productExpServiceController
                .getFundPrePaymentDetail(corrID, fundPaymentDetailRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getFundRule());
        Assert.assertNotNull(actualResult.getBody().getData().getDepositAccountList());
        Assert.assertNotNull(actualResult.getBody().getData().getFundHolidayList());
    }

    @Test
    public void testGetFundAccountDetailNull() {
        initAccountDetailBody();
        initFundRuleBody();

        FundAccountResponse fundAccountResponse;
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");

        AccountDetailBody accountDetailBody = null;
        FundRuleBody fundRuleBody = null;

        try {
            fundAccountResponse = new FundAccountResponse();

            FundAccountDetail fundAccountDetail = mappingResponse(accountDetailBody, fundRuleBody);
            fundAccountResponse.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundFFSAndValidation() {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");

        FfsRsAndValidation fundRsAndValidation;

        try {
            fundRsAndValidation = new FfsRsAndValidation();
            FfsData body = new FfsData();
            body.setFactSheetData("fdg;klghbdf;jbneoa;khnd'flbkndflkhnreoid;bndzfklbnoresibndlzfk[bnseriohnbodkzfvndsogb");
            fundRsAndValidation.setBody(body);
            when(productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody)).thenReturn(fundRsAndValidation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    public void getFundFFSAndValidationFail() {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");

        FfsRsAndValidation fundRsAndValidation;
        try {
            fundRsAndValidation = new FfsRsAndValidation();
            fundRsAndValidation.setError(true);
            fundRsAndValidation.setErrorCode(ProductsExpServiceConstant.SERVICE_OUR_CLOSE);
            fundRsAndValidation.setErrorMsg(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_MESSAGE);
            fundRsAndValidation.setErrorDesc(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_DESC);

            when(productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody)).thenReturn(fundRsAndValidation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        assertEquals(HttpStatus.BAD_REQUEST, actualResult.getStatusCode());
    }

    @Test
    public void getFundFFSAndValidationError() {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("N");
        ffsRequestBody.setOrderType("1");

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        assertEquals(HttpStatus.BAD_REQUEST, actualResult.getStatusCode());
    }

    @Test
    public void getFundFFSAndValidationException() {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");

        when(productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody)).thenThrow(MockitoException.class);

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void validateAlternativeSaleAndSwitchException() {
        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("SCBTMF");
        alternativeRq.setFundHouseCode("SCBAM");
        alternativeRq.setCrmId("001100000000000000000012025950");
        alternativeRq.setProcessFlag("Y");
        alternativeRq.setOrderType("2");

        when(productsExpService.validateAlternativeSellAndSwitch(corrID, alternativeRq)).thenThrow(MockitoException.class);

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(corrID, alternativeRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void validateAlternativeSaleAndSwitchError() {
        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("SCBTMF");
        alternativeRq.setFundHouseCode("SCBAM");
        alternativeRq.setCrmId("001100000000000000000012025950");
        alternativeRq.setProcessFlag("Y");
        alternativeRq.setOrderType("2");
        alternativeRq.setUnitHolderNo("PT00000000000");

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(corrID, alternativeRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void validateAlternativeSaleAndSwitch() {
        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("SCBTMF");
        alternativeRq.setFundHouseCode("SCBAM");
        alternativeRq.setCrmId("001100000000000000000012025950");
        alternativeRq.setProcessFlag("Y");
        alternativeRq.setOrderType("2");
        alternativeRq.setUnitHolderNo("PT00000000000");

        FundResponse fundRsAndValidation;

        try {
            fundRsAndValidation = new FundResponse();
            fundRsAndValidation.setError(false);
            fundRsAndValidation.setErrorCode("0000");
            fundRsAndValidation.setErrorDesc("success");
            fundRsAndValidation.setErrorMsg("success");

            when(productsExpService.validateAlternativeSellAndSwitch(corrID, alternativeRq)).thenReturn(fundRsAndValidation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(corrID, alternativeRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    public void getFundListException() {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRq fundListRq = new FundListRq();
        fundListRq.setCrmId("12343455555");
        fundListRq.setUnitHolderNo(unitStr);

        when(productsExpService.getFundList(corrID, fundListRq)).thenThrow(MockitoException.class);

        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(corrID, fundListRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundListNotFound() {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRq fundListRq = new FundListRq();
        fundListRq.setCrmId("12343455555");
        fundListRq.setUnitHolderNo(unitStr);

        try {
            when(productsExpService.getFundList(corrID, fundListRq)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(corrID, fundListRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundList() {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRq fundListRq = new FundListRq();
        fundListRq.setCrmId("12343455555");
        fundListRq.setUnitHolderNo(unitStr);

        List<FundClassListInfo> list = new ArrayList<>();
        FundClassListInfo fundClassListInfo = new FundClassListInfo();
        fundClassListInfo.setFundCode("ABCC");
        list.add(fundClassListInfo);

        try {
            when(productsExpService.getFundList(corrID, fundListRq)).thenReturn(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(corrID, fundListRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    void testDataNotFoundError() {
        TmbOneServiceResponse<FundPaymentDetailRs> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus status = new TmbStatus();
        status.setService("products-exp-service");
        oneServiceResponse.setStatus(status);
        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> response = productExpServiceController.dataNotFoundError(oneServiceResponse);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testErrorResponse() {
        TmbOneServiceResponse<FundResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        FundResponse data = new FundResponse();
        data.setError(true);
        oneServiceResponse.setData(data);
        ResponseEntity<TmbOneServiceResponse<FundResponse>> errorResponse = productExpServiceController.errorResponse(oneServiceResponse, data);
        assertEquals(400, errorResponse.getStatusCodeValue());
    }

    @Test
    void testGetFundPrePaymentDetailNull() {
        when(productsExpService.getFundPrePaymentDetail(anyString(), any())).thenReturn(null);

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> result = productExpServiceController.getFundPrePaymentDetail("correlationId", new FundPaymentDetailRq());
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    void should_return_SuggestAllocationDTO_when_call_get_fund_suggest_allocation_given_correlation_id_and_crd_id() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = corrID;
        SuggestAllocationBodyRequest suggestAllocationBodyRequest = SuggestAllocationBodyRequest.builder()
                .crmId("00000018592884")
                .build();

        SuggestAllocationDTO suggestAllocationDTO = mapper.readValue(Paths.get("src/test/resources/investment/fund/suggest_allocation_dto.json").toFile(), SuggestAllocationDTO.class);
        when(productsExpService.getSuggestAllocation(correlationId, suggestAllocationBodyRequest.getCrmId())).thenReturn(suggestAllocationDTO);

        //When
        ResponseEntity<TmbOneServiceResponse<SuggestAllocationDTO>> actual = productExpServiceController.getFundSuggestAllocation(correlationId, suggestAllocationBodyRequest);

        //Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(suggestAllocationDTO, actual.getBody().getData());
    }

    @Test
    void should_return_not_found_when_call_get_fund_suggest_allocation_given_correlation_id_and_crd_id() {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = corrID;
        SuggestAllocationBodyRequest fundCodeRequestBody = SuggestAllocationBodyRequest.builder()
                .crmId("00000018592884")
                .build();
        when(productsExpService.getSuggestAllocation(correlationId, fundCodeRequestBody.getCrmId())).thenThrow(RuntimeException.class);
        //When
        ResponseEntity<TmbOneServiceResponse<SuggestAllocationDTO>> actual = productExpServiceController.getFundSuggestAllocation(correlationId, fundCodeRequestBody);

        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertNull(actual.getBody().getData());
    }
}