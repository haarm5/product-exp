package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundClass;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSearch;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountDetail;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundHolidayClassList;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementList;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(JUnit4.class)
public class UtilMapTest {

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testValidateTMBResponse() {
        /* accountDetailResponse  */
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("1234");
        fundDetail.setCost("1234");
        fundDetail.setEnglishFundName("card");
        fundDetail.setInvestmentValue("12345");

        AccountDetailResponse accountDetailResponse = new AccountDetailResponse();
        accountDetailResponse.setFundDetail(fundDetail);

        /* fundRuleResponse */
        FundRuleResponse fundRuleResponse = new FundRuleResponse();
        List<FundRuleInfoList> fundRuleList = new ArrayList<>();
        for (FundRuleInfoList ruleInfoList : fundRuleList) {
            ruleInfoList.setFundHouseCode("123");
            fundRuleList.add(ruleInfoList);
        }
        fundRuleResponse.setFundRuleInfoList(fundRuleList);

        /* statementResponse */
        List<StatementList> list = new ArrayList<>();
        for (StatementList statementList : list) {
            statementList.setTranTypeEN("Normal");
            statementList.setFundCode("1234");
            statementList.setEffectiveDate("28-03-2021");
            list.add(statementList);
        }
        StatementResponse statementResponse = new StatementResponse();
        statementResponse.setTotalRecord("10");
        statementResponse.setStatementList(list);

        FundAccountResponse result = UtilMap.validateTMBResponse(accountDetailResponse, fundRuleResponse, statementResponse);
         List<FundRuleInfoList> fundRuleInfoList = result.getDetails().getFundRuleInfoList();
        Assert.assertEquals(true, fundRuleInfoList.isEmpty());


    }

    @Test
    void testMappingResponse() {
        /* accountDetailResponse */
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("1234");
        fundDetail.setCost("1234");
        fundDetail.setEnglishFundName("card");
        fundDetail.setInvestmentValue("12345");

        AccountDetailResponse accountDetailResponse = new AccountDetailResponse();
        accountDetailResponse.setFundDetail(fundDetail);

        /* fundRuleResponse */
        FundRuleResponse fundRuleResponse = new FundRuleResponse();
        List<FundRuleInfoList> fundRuleList = new ArrayList<>();
        for (FundRuleInfoList ruleInfoList : fundRuleList) {
            ruleInfoList.setFundHouseCode("123");
            fundRuleList.add(ruleInfoList);
        }
        fundRuleResponse.setFundRuleInfoList(fundRuleList);

        /* statementResponse */
        StatementResponse statementResponse = new StatementResponse();
        statementResponse.setTotalRecord("10");
        List<StatementList> list = new ArrayList<>();
        for (StatementList statementList : list) {
            statementList.setTranTypeEN("Normal");
            statementList.setFundCode("1234");
            statementList.setEffectiveDate("28-03-2021");
            list.add(statementList);
        }
        statementResponse.setStatementList(list);

        FundAccountDetail result = UtilMap.mappingResponse(accountDetailResponse, fundRuleResponse, statementResponse);
        List<FundRuleInfoList> fundRuleInfoList = result.getFundRuleInfoList();
        Assert.assertEquals(true, fundRuleInfoList.isEmpty());
    }

    @Test
    void testMappingPaymentResponse() {
        FundRuleResponse fundRuleResponse = new FundRuleResponse();
        List<FundRuleInfoList> fundRuleList = new ArrayList<>();
        for (FundRuleInfoList ruleInfoList : fundRuleList) {
            ruleInfoList.setFundHouseCode("123");
            fundRuleList.add(ruleInfoList);
        }
        fundRuleResponse.setFundRuleInfoList(fundRuleList);
        FundHolidayBody fundHolidayBody = new FundHolidayBody();
        List<FundHolidayClassList> list = new ArrayList<>();
        for (FundHolidayClassList fundHolidayClassList : list) {
            fundHolidayClassList.setFundCode("1234");
            fundHolidayClassList.setHolidayDesc("Enjoy");
            fundHolidayClassList.setHolidayDate("12-12-2012");
            fundHolidayClassList.setFundHouseCode("1234");
        }
        fundHolidayBody.setFundClassList(list);
        List<CommonData> responseCommon = new ArrayList();
        CommonData data = new CommonData();
        for (CommonData common : responseCommon) {
            common.setAccount221Url("www.gmail.com");
            common.setAccount290Url("www.123.com");
            responseCommon.add(common);
        }
        data.setAccount221Url("www.gmail.com");
        data.setChannel("1234");
        responseCommon.add(data);
        Assert.assertNotEquals("www.gmail.com", data.getAccount290Url());
    }

    @Test
    void testMappingAccount() {
        CommonData data = new CommonData();
        data.setChannel("1234");
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundHouseCode("1234");
        FundPaymentDetailResponse fundPaymentDetailResponse = new FundPaymentDetailResponse();
        fundPaymentDetailResponse.setFundRule(list);
        List<DepositAccount> depositAccountList = UtilMap.mappingAccount(Arrays.asList(data), "responseCustomerExp", true);
        fundPaymentDetailResponse.setDepositAccountList(depositAccountList);
        data.setAccount290Url("1234");
        assertNotEquals(data.getAccount290Url(), depositAccountList);
    }

    @Test
    void testConvertAccountType() {
        String result = UtilMap.convertAccountType("productType");
        Assert.assertEquals("", result);
    }

    @Test
    void testIsBusinessClose() {
        boolean result = UtilMap.isBusinessClose("startTime", "endTime");
        Assert.assertEquals(false, result);
    }

    @Test
    void testCreateHeader2() {
        String correlationId = "1234";
        Map<String, String> result = UtilMap.createHeader(correlationId);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, "1234");
        Assert.assertEquals(hashMap, result);
    }

    @Test
    void testIsSuitabilityExpire() {
        SuitabilityInfo suitabilityInfo = new SuitabilityInfo();
        suitabilityInfo.setFxFlag("1234");
        suitabilityInfo.setSuitValidation("1");
        boolean result = UtilMap.isSuitabilityExpire(suitabilityInfo);
        Assert.assertEquals(false, result);
    }

    @Test
    void testIsCustIDExpired() {
        boolean result = UtilMap.isCustIdExpired(null);
        Assert.assertEquals(false, result);
    }

    @Test
    void testIsCASADormant() {
        boolean result = UtilMap.isCASADormant("responseCustomerExp");
        Assert.assertEquals(false, result);
    }

    @Test
    void testDeleteColonDateFormat() {
        String result = UtilMap.deleteColonDateFormat("timeHHmm");
        Assert.assertEquals("timeHHmm", result);
    }

    @Test
    void testMappingFundListData() {
        FundClass fundClass = new FundClass();
        fundClass.setFundClassCode("1234");
        List<FundClass> result = UtilMap.mappingFundListData(Arrays.asList(fundClass));
        Assert.assertNotEquals(Arrays.asList(fundClass), result);
    }

    @Test
    void testMappingFundSearchListData() {
        FundClass fundClass = new FundClass();
        fundClass.setFundClassCode("1234");
        List<FundSearch> result = UtilMap.mappingFundSearchListData(Arrays.asList(fundClass));
        FundSearch fundSearch = new FundSearch();
        fundSearch.setFundCode("1234");
        Assert.assertNotEquals(Arrays.asList(fundSearch), result);
    }

    @Test
    void testMappingRequestFundAcc() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("1234");
        FundAccountRequestBody result = UtilMap.mappingRequestFundAccount(fundAccountRequest);
        FundAccountRequestBody requestBody = new FundAccountRequestBody();
        requestBody.setFundCode("1234");
        Assert.assertEquals(requestBody.getFundCode(), result.getFundCode());
    }

    @Test
    void testMappingRequestFundRule() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("1234");
        FundRuleRequestBody result = UtilMap.mappingRequestFundRule(fundAccountRequest);
        FundRuleRequestBody requestBody = new FundRuleRequestBody();
        requestBody.setFundCode("1234");
        Assert.assertEquals(requestBody.getFundCode(), result.getFundCode());
    }

    @Test
    void testMappingRequestStmtByPort() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("1234");
        OrderStmtByPortRequest result = UtilMap.mappingRequestStmtByPort(fundAccountRequest, "startPage", "endPage");
        OrderStmtByPortRequest portRequest = new OrderStmtByPortRequest();
        portRequest.setFundCode("1234");
        Assert.assertEquals(portRequest.getFundCode(), result.getFundCode());
    }

    @Test
    void testMapTmbOneServiceResponse() {
        TmbOneServiceResponse result = UtilMap.mapTmbOneServiceResponse(null);
        Assert.assertEquals(null, result);
    }

    @Test
    void should_return_full_format_of_crm_id_when_call_full_crm_id_format_given_half_crm_id() {
        // Given
        // When
        String actual = UtilMap.fullCrmIdFormat("00000000002914");

        // Then
        assertEquals("001100000000000000000000002914", actual);
    }

    @Test
    void should_return_full_format_of_crm_id_when_call_full_crm_id_format_given_full_crm_id() {
        // Given
        // When
        String actual = UtilMap.fullCrmIdFormat("001100000000000000000000002914");

        // Then
        assertEquals("001100000000000000000000002914", actual);
    }

    @Test
    void should_return_half_format_of_crm_id_when_call_half_crm_id_format_given_half_crm_id() {
        // Given
        // When
        String actual = UtilMap.halfCrmIdFormat("00000000002914");

        // Then
        assertEquals("00000000002914", actual);
    }

    @Test
    void should_return_half_format_of_crm_id_when_call_half_crm_id_format_given_full_crm_id() {
        // Given
        // When
        String actual = UtilMap.halfCrmIdFormat("001100000000000000000000002914");

        // Then
        assertEquals("00000000002914", actual);
    }
}