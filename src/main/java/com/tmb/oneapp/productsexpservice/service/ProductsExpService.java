package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.CommonTime;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.fundallocation.*;
import com.tmb.oneapp.productsexpservice.enums.FatcaErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.*;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.*;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.request.AlternativeRequest;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.request.CountToBeProcessOrderRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.request.FundAllocationRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.PtesBodyRequest;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.request.suitability.SuitabilityBody;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.response.CountOrderProcessingResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.response.FundAllocationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.response.FundSuggestAllocationList;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustomerFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.fundsummary.FundSummaryByPortResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * ProductsExpService class will get fund Details from MF Service
 */
@Service
public class ProductsExpService {

    @Value("${com.tmb.oneapp.service.activity.topic.name}")
    private String topicName;

    private static final TMBLogger<ProductsExpService> logger = new TMBLogger<>(ProductsExpService.class);

    private final InvestmentRequestClient investmentRequestClient;

    private final AccountRequestClient accountRequestClient;

    private final CommonServiceClient commonServiceClient;

    private final ProductExpAsyncService productExpAsyncService;

    private final CustomerExpServiceClient customerExpServiceClient;

    private final CustomerServiceClient customerServiceClient;

    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public ProductsExpService(InvestmentRequestClient investmentRequestClient,
                              AccountRequestClient accountRequestClient,
                              KafkaProducerService kafkaProducerService,
                              CommonServiceClient commonServiceClient,
                              ProductExpAsyncService productExpAsyncService,
                              CustomerExpServiceClient customerExpServiceClient,
                              CustomerServiceClient customerServiceClient) {

        this.investmentRequestClient = investmentRequestClient;
        this.kafkaProducerService = kafkaProducerService;
        this.accountRequestClient = accountRequestClient;
        this.commonServiceClient = commonServiceClient;
        this.productExpAsyncService = productExpAsyncService;
        this.customerExpServiceClient = customerExpServiceClient;
        this.customerServiceClient = customerServiceClient;
    }

    /**
     * Generic Method to call MF Service getFundAccDetail
     *
     * @param fundAccountRequest
     * @param correlationId
     * @return
     */
    @LogAround
    public FundAccountResponse getFundAccountDetail(String correlationId, FundAccountRequest fundAccountRequest) {
        FundAccountResponse fundAccountResponse;
        FundAccountRequestBody fundAccountRequestBody = UtilMap.mappingRequestFundAcc(fundAccountRequest);
        FundRuleRequestBody fundRuleRequestBody = UtilMap.mappingRequestFundRule(fundAccountRequest);
        OrderStmtByPortRequest orderStmtByPortRequest = UtilMap.mappingRequestStmtByPort(fundAccountRequest,
                ProductsExpServiceConstant.FIXED_START_PAGE, ProductsExpServiceConstant.FIXED_END_PAGE);

        Map<String, String> header = UtilMap.createHeader(correlationId);
        try {
            CompletableFuture<AccountDetailBody> fetchFundAccountDetail = productExpAsyncService.fetchFundAccountDetail(header, fundAccountRequestBody);
            CompletableFuture<FundRuleBody> fetchFundRule = productExpAsyncService.fetchFundRule(header, fundRuleRequestBody);
            CompletableFuture<StatementResponse> fetchStmtByPort = productExpAsyncService.fetchStatementByPort(header, orderStmtByPortRequest);
            CompletableFuture.allOf(fetchFundAccountDetail, fetchFundRule, fetchStmtByPort);

            AccountDetailBody accountDetailBody = fetchFundAccountDetail.get();
            FundRuleBody fundRuleBody = fetchFundRule.get();
            StatementResponse statementResponse = fetchStmtByPort.get();
            fundAccountResponse = UtilMap.validateTMBResponse(accountDetailBody, fundRuleBody, statementResponse);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return null;
        }
        return fundAccountResponse;
    }

    /**
     * Get fund summary fund summary response.
     *
     * @param correlationId the correlation id
     * @param rq            the rq
     * @return the fund summary response
     */
    @LogAround
    public FundSummaryBody getFundSummary(String correlationId, FundSummaryRq rq) {
        FundSummaryBody result = new FundSummaryBody();
        ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> fundSummaryData;
        UnitHolder unitHolder = new UnitHolder();
        ResponseEntity<TmbOneServiceResponse<FundSummaryByPortResponse>> summaryByPortResponse;
        Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
        ResponseEntity<TmbOneServiceResponse<CountOrderProcessingResponseBody>> countOrderProcessingResponse;
        try {
            String crmId = rq.getCrmId();
            List<String> ports = getPortList(crmId, invHeaderReqParameter, true);
            result.setPortsUnitHolder(ports);
            unitHolder.setUnitHolderNo(ports.stream().map(String::valueOf).collect(Collectors.joining(",")));
            fundSummaryData = investmentRequestClient.callInvestmentFundSummaryService(invHeaderReqParameter, unitHolder);
            summaryByPortResponse = investmentRequestClient.callInvestmentFundSummaryByPortService(invHeaderReqParameter, unitHolder);
            countOrderProcessingResponse = investmentRequestClient.callInvestmentCountProcessOrderService(invHeaderReqParameter,
                    CountToBeProcessOrderRequestBody.builder().serviceType("1").rm(crmId).build());

            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE + "{}", fundSummaryData);

            if (HttpStatus.OK.value() == fundSummaryData.getStatusCode().value()) {
                var body = fundSummaryData.getBody();
                var summaryByPort = summaryByPortResponse.getBody();
                this.setFundSummaryBody(result, ports, body, summaryByPort);
            }

            result.setCountProcessedOrder("0");
            if (HttpStatus.OK.value() == countOrderProcessingResponse.getStatusCode().value()) {
                result.setCountProcessedOrder(countOrderProcessingResponse.getBody().getData().getCountProcessOrder());
            }
            return result;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return null;
        }
    }

    public List<String> getPortList(String crmId, Map<String, String> invHeaderReqParameter, boolean isIncludePtesPortfolio) throws com.fasterxml.jackson.core.JsonProcessingException {
        List<String> ports = new ArrayList<>();
        List<String> ptestPortList = new ArrayList<>();
        PtesBodyRequest ptesBodyRequest = new PtesBodyRequest();
        ptesBodyRequest.setRmNumber(crmId);
        String portData = customerExpServiceClient.getAccountSaving(invHeaderReqParameter.get(ProductsExpServiceConstant.X_CORRELATION_ID), crmId);

        logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, portData);

        if (!StringUtils.isEmpty(portData)) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(portData, JsonNode.class);
            JsonNode dataNode = node.get("data");
            JsonNode portList = dataNode.get("mutual_fund_accounts");
            ports = mapper.readValue(portList.toString(), new TypeReference<List<String>>() {
            });
        }
        if (isIncludePtesPortfolio) {
            ResponseEntity<TmbOneServiceResponse<List<PtesDetail>>> ptestDetailResult = investmentRequestClient.getPtesPort(invHeaderReqParameter, ptesBodyRequest);

            Optional<List<PtesDetail>> ptesDetailList = Optional.ofNullable(ptestDetailResult)
                    .map(ResponseEntity::getBody)
                    .map(TmbOneServiceResponse::getData);
            if (ptesDetailList.isPresent()) {
                ptestPortList = ptesDetailList.get().stream()
                        .filter(ptesDetail -> ProductsExpServiceConstant.PTES_PORT_FOLIO_FLAG.equalsIgnoreCase(ptesDetail.getPortfolioFlag()))
                        .map(PtesDetail::getPortfolioNumber)
                        .collect(Collectors.toList());
            }
        }
        ports.addAll(ptestPortList);
        return ports;
    }

    /***
     * Set The FundSummaryBody
     * @param result
     * @param body
     * @param summaryByPort
     */
    private void setFundSummaryBody(FundSummaryBody result, List<String> ports, TmbOneServiceResponse<FundSummaryResponse> body, TmbOneServiceResponse<FundSummaryByPortResponse> summaryByPort) {
        if (body != null) {
            FundClassList fundClassList = body.getData().getBody().getFundClassList();
            List<FundClass> fundClass = fundClassList.getFundClass();
            List<FundClass> fundClassData = UtilMap.mappingFundListData(fundClass);
            List<FundSearch> searchList = UtilMap.mappingFundSearchListData(fundClass);
            result.setFundClass(fundClassData);
            result.setSearchList(searchList);
            result.setFundClassList(null);
            result.setFeeAsOfDate(body.getData().getBody().getFeeAsOfDate());
            result.setPercentOfFundType(body.getData().getBody().getPercentOfFundType());
            result.setSumAccruedFee(body.getData().getBody().getSumAccruedFee());
            result.setUnrealizedProfitPercent(body.getData().getBody().getUnrealizedProfitPercent());
            result.setSummaryMarketValue(body.getData().getBody().getSummaryMarketValue());
            result.setSummaryUnrealizedProfit(body.getData().getBody().getSummaryUnrealizedProfit());
            result.setSummarySmartPortUnrealizedProfitPercent(body.getData().getBody().getSummarySmartPortUnrealizedProfitPercent());
            result.setSummarySmartPortMarketValue(body.getData().getBody().getSummarySmartPortMarketValue());
            result.setSummarySmartPortUnrealizedProfit(body.getData().getBody().getSummarySmartPortUnrealizedProfit());
            result.setSummarySmartPortUnrealizedProfitPercent(body.getData().getBody().getSummarySmartPortUnrealizedProfitPercent());
            List<FundClass> smartPort = fundClassData.stream()
                    .filter(port -> ProductsExpServiceConstant.SMART_PORT_CODE.equalsIgnoreCase(port.getFundClassCode()))
                    .collect(Collectors.toList());
            List<FundClass> ptPort = fundClassData.stream()
                    .filter(port -> !ProductsExpServiceConstant.SMART_PORT_CODE.equalsIgnoreCase(port.getFundClassCode()))
                    .collect(Collectors.toList());
            result.setSmartPortList(smartPort);
            result.setPtPortList(ptPort);

            if (summaryByPort != null && summaryByPort.getData() != null && summaryByPort.getData().getBody() != null &&
                    !summaryByPort.getData().getBody().getPortfolioList().isEmpty()) {
                result.setSummaryByPort(summaryByPort.getData().getBody().getPortfolioList());
            }
            List<String> ptPorts = ports.stream().filter(port -> port.startsWith("PT")).collect(Collectors.toList());
            List<String> ptestPorts = ports.stream().filter(port -> port.startsWith("PTES")).collect(Collectors.toList());
            if (!smartPort.isEmpty()) {
                result.setIsSmartPort(Boolean.TRUE);
            }
            if (!ptPorts.isEmpty()) {
                result.setIsPt(Boolean.TRUE);
            }
            if (!ptestPorts.isEmpty()) {
                result.setIsPtes(Boolean.TRUE);
            }
        }
    }

    /**
     * Generic Method to call MF Service getFundAccDetail
     *
     * @param fundPaymentDetailRequest
     * @param correlationId
     * @return
     */
    @LogAround
    public FundPaymentDetailResponse getFundPrePaymentDetail(String correlationId, FundPaymentDetailRequest fundPaymentDetailRequest) {
        FundRuleRequestBody fundRuleRequestBody = UtilMap.mappingRequestFundRule(fundPaymentDetailRequest);
        Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
        FundPaymentDetailResponse fundPaymentDetailResponse;
        try {
            CompletableFuture<FundRuleBody> fetchFundRule = productExpAsyncService.fetchFundRule(invHeaderReqParameter, fundRuleRequestBody);
            CompletableFuture<FundHolidayBody> fetchFundHoliday = productExpAsyncService.fetchFundHoliday(invHeaderReqParameter, fundRuleRequestBody.getFundCode());
            CompletableFuture<String> fetchCustomerExp = productExpAsyncService.fetchCustomerExp(invHeaderReqParameter, fundPaymentDetailRequest.getCrmId());
            CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsyncService.fetchCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);

            CompletableFuture.allOf(fetchFundRule, fetchFundHoliday, fetchCustomerExp, fetchCommonConfigByModule);
            FundRuleBody fundRuleBody = fetchFundRule.get();
            FundHolidayBody fundHolidayBody = fetchFundHoliday.get();
            String customerExp = fetchCustomerExp.get();
            List<CommonData> commonDataList = fetchCommonConfigByModule.get();

            UtilMap map = new UtilMap();
            fundPaymentDetailResponse = map.mappingPaymentResponse(fundRuleBody, fundHolidayBody, commonDataList, customerExp);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return null;
        }
        return fundPaymentDetailResponse;
    }

    /**
     * Generic Method to call MF Service getFundFFSAndValidation
     *
     * @param ffsRequestBody
     * @param correlationId
     * @return FfsRsAndValidation
     */
    @LogAround
    public FfsRsAndValidation getFundFFSAndValidation(String correlationId, FfsRequestBody ffsRequestBody) {
        FfsRsAndValidation ffsRsAndValidation = new FfsRsAndValidation();
        FundResponse fundResponse = new FundResponse();
        fundResponse = isServiceHour(correlationId, fundResponse);
        if (!fundResponse.isError()) {
            String fatcaFlag = getFatcaFlag(correlationId, ffsRequestBody.getCrmId());
            ffsRsAndValidation = validationAlternativeFlow(correlationId, ffsRequestBody, ffsRsAndValidation, fatcaFlag);

        } else {
            errorData(ffsRsAndValidation, fundResponse);
        }
        return ffsRsAndValidation;
    }

    void errorData(FfsRsAndValidation ffsRsAndValidation, FundResponse fundResponse) {
        ffsRsAndValidation.setError(true);
        ffsRsAndValidation.setErrorCode(fundResponse.getErrorCode());
        ffsRsAndValidation.setErrorMsg(fundResponse.getErrorMsg());
        ffsRsAndValidation.setErrorDesc(fundResponse.getErrorDesc());
    }

    void ffsData(FfsRsAndValidation ffsRsAndValidation, ResponseEntity<TmbOneServiceResponse<FfsResponse>> responseEntity) {
        FfsData ffsData = new FfsData();
        ffsData.setFactSheetData(responseEntity.getBody().getData().getBody().getFactSheetData());
        ffsRsAndValidation.setBody(ffsData);
    }

    /**
     * Generic Method to validate AlternativeSellAndSwitch
     *
     * @param alternativeRequest
     * @param correlationId
     * @return FundResponse
     */
    @LogAround
    public FundResponse validateAlternativeSellAndSwitch(String correlationId, AlternativeRequest alternativeRequest) {
        FundResponse fundResponse = new FundResponse();
        fundResponse = isServiceHour(correlationId, fundResponse);
        if (!fundResponse.isError()) {
            FfsRequestBody ffsRequestBody = new FfsRequestBody();
            ffsRequestBody.setUnitHolderNumber(alternativeRequest.getUnitHolderNumber());
            ffsRequestBody.setProcessFlag(alternativeRequest.getProcessFlag());
            ffsRequestBody.setCrmId(alternativeRequest.getCrmId());
            String fatcaFlag = getFatcaFlag(correlationId, ffsRequestBody.getCrmId());
            fundResponse = validationAlternativeSellAndSwitchFlow(correlationId, ffsRequestBody, fundResponse, fatcaFlag);
            if (!StringUtils.isEmpty(fundResponse) && !fundResponse.isError()) {
                fundResponseSuccess(fundResponse);
            }
        }
        return fundResponse;
    }

    private String getFatcaFlag(String correlationId, String crmId) {
        CrmSearchBody request = CrmSearchBody.builder()
                .searchType(ProductsExpServiceConstant.SEARCH_TYPE)
                .searchValue(crmId)
                .build();
        ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> response =
                customerServiceClient.customerSearch(crmId, correlationId, request);
        return response.getBody().getData().get(0).getFatcaFlag();
    }

    /**
     * @param fundResponse
     */
    void fundResponseSuccess(FundResponse fundResponse) {
        fundResponse.setError(false);
        fundResponse.setErrorCode(ProductsExpServiceConstant.SUCCESS_CODE);
        fundResponse.setErrorMsg(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        fundResponse.setErrorDesc(ProductsExpServiceConstant.SUCCESS);
    }

    /**
     * To validate Alternative case and verify expire-citizen id
     *
     * @param ffsRequestBody
     * @param correlationId
     * @param ffsRsAndValidation
     * @return FfsRsAndValidation
     */
    @LogAround
    public FfsRsAndValidation validationAlternativeFlow(String correlationId, FfsRequestBody ffsRequestBody,
                                                        FfsRsAndValidation ffsRsAndValidation, String fatcaFlag) {
        final boolean isNotValid = true;
        boolean isStoped = false;
        if (isCASADormant(correlationId, ffsRequestBody)) {
            ffsRsAndValidation.setError(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_DESC);
            isStoped = true;
        }
        if (!isStoped && isSuitabilityExpired(correlationId, ffsRequestBody)) {
            ffsRsAndValidation.setError(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.SUITABILITY_EXPIRED_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.SUITABILITY_EXPIRED_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.SUITABILITY_EXPIRED_DESC);
            isStoped = true;
        }
        if (!isStoped && isCustIDExpired(ffsRequestBody)) {
            fundResponseError(ffsRsAndValidation, isNotValid);
            isStoped = true;
        }
        if (!isStoped && isBusinessClose(correlationId, ffsRequestBody)) {
            errorResponse(ffsRsAndValidation, isNotValid);
            isStoped = true;
        }
        if (!isStoped && fatcaFlag.equalsIgnoreCase("0")) {
            funResponseMapping(ffsRsAndValidation,
                    FatcaErrorEnums.CUSTOMER_NOT_FILLED_IN.getCode(),
                    FatcaErrorEnums.CUSTOMER_NOT_FILLED_IN.getMsg(),
                    FatcaErrorEnums.CUSTOMER_NOT_FILLED_IN.getDesc());
        } else if (!isStoped && !fatcaFlag.equalsIgnoreCase("N")) {
            funResponseMapping(ffsRsAndValidation,
                    FatcaErrorEnums.USNATIONAL.getCode(),
                    FatcaErrorEnums.USNATIONAL.getMsg(),
                    FatcaErrorEnums.USNATIONAL.getDesc());
        }
        return ffsRsAndValidation;
    }

    void errorResponse(FfsRsAndValidation ffsRsAndValidation, boolean isNotValid) {
        ffsRsAndValidation.setError(isNotValid);
        ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_CODE);
        ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_MESSAGE);
        ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_DESC);
    }

    /**
     * To validate Alternative case and verify expire-citizen id
     *
     * @param ffsRequestBody
     * @param correlationId
     * @param fundResponse
     * @return FundResponse
     */
    @LogAround
    public FundResponse validationAlternativeSellAndSwitchFlow(String correlationId, FfsRequestBody ffsRequestBody,
                                                               FundResponse fundResponse, String fatcaFlag) {
        final boolean isNotValid = true;
        if (isSuitabilityExpired(correlationId, ffsRequestBody)) {
            fundResponse.setError(isNotValid);
            fundResponse.setErrorCode(ProductsExpServiceConstant.SUITABILITY_EXPIRED_CODE);
            fundResponse.setErrorMsg(ProductsExpServiceConstant.SUITABILITY_EXPIRED_MESSAGE);
            fundResponse.setErrorDesc(ProductsExpServiceConstant.SUITABILITY_EXPIRED_DESC);
            return fundResponse;
        }
        if (isCustIDExpired(ffsRequestBody)) {
            fundResponseError(fundResponse, isNotValid);
            return fundResponse;
        }

        if (fatcaFlag.equalsIgnoreCase("0")) {
            funResponseMapping(fundResponse,
                    FatcaErrorEnums.CUSTOMER_NOT_FILLED_IN.getCode(),
                    FatcaErrorEnums.CUSTOMER_NOT_FILLED_IN.getMsg(),
                    FatcaErrorEnums.CUSTOMER_NOT_FILLED_IN.getDesc());
        } else if (!fatcaFlag.equalsIgnoreCase("N")) {
            funResponseMapping(fundResponse,
                    FatcaErrorEnums.USNATIONAL.getCode(),
                    FatcaErrorEnums.USNATIONAL.getMsg(),
                    FatcaErrorEnums.USNATIONAL.getDesc());
        }
        return fundResponse;
    }

    private void funResponseMapping(FundResponse fundResponse, String code, String msg, String desc) {
        fundResponse.setError(true);
        fundResponse.setErrorCode(code);
        fundResponse.setErrorMsg(msg);
        fundResponse.setErrorDesc(desc);
    }

    /**
     * @param fundResponse
     * @param isNotValid
     */
    void fundResponseError(FundResponse fundResponse, boolean isNotValid) {
        fundResponse.setError(isNotValid);
        fundResponse.setErrorCode(ProductsExpServiceConstant.ID_EXPIRED_CODE);
        fundResponse.setErrorMsg(ProductsExpServiceConstant.ID_EXPIRED_MESSAGE);
        fundResponse.setErrorDesc(ProductsExpServiceConstant.ID_EXPIRED_DESC);
    }

    /**
     * Method isServiceHour Query service hour from common-service
     *
     * @param correlationId
     * @param fundResponse
     */
    public FundResponse isServiceHour(String correlationId, FundResponse fundResponse) {
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommon = null;
        try {
            responseCommon = commonServiceClient.getCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCommon);
            if (!StringUtils.isEmpty(responseCommon)) {
                List<CommonData> commonDataList = responseCommon.getBody().getData();
                CommonData commonData = commonDataList.get(0);
                CommonTime noneServiceHour = commonData.getNoneServiceHour();
                if (UtilMap.isBusinessClose(noneServiceHour.getStart(), noneServiceHour.getEnd())) {
                    fundResponseData(fundResponse, noneServiceHour);
                }
            }
            return fundResponse;
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            fundResponse.setError(true);
            fundResponse.setErrorCode(ProductsExpServiceConstant.SERVICE_NOT_READY);
            fundResponse.setErrorMsg(ProductsExpServiceConstant.SERVICE_NOT_READY_MESSAGE);
            fundResponse.setErrorDesc(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC);
            return fundResponse;
        }
    }

    /**
     * @param fundResponse
     * @param noneServiceHour
     */
    void fundResponseData(FundResponse fundResponse, CommonTime noneServiceHour) {
        fundResponse.setError(true);
        fundResponse.setErrorCode(ProductsExpServiceConstant.SERVICE_OUR_CLOSE);
        fundResponse.setErrorMsg(noneServiceHour.getStart());
        fundResponse.setErrorDesc(noneServiceHour.getEnd());
    }

    /**
     * Method isBusinessClose for check cut of time from fundRule
     *
     * @param correlationId
     * @param ffsRequestBody
     */
    public boolean isBusinessClose(String correlationId, FfsRequestBody ffsRequestBody) {
        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(ffsRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(ffsRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity;
        try {
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
            responseEntity = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
            if (!StringUtils.isEmpty(responseEntity) &&
                    HttpStatus.OK == responseEntity.getStatusCode()) {
                FundRuleBody fundRuleBody = responseEntity.getBody().getData();
                FundRuleInfoList fundRuleInfoList = fundRuleBody.getFundRuleInfoList().get(0);
                return (UtilMap.isBusinessClose(fundRuleInfoList.getTimeStart(), fundRuleInfoList.getTimeEnd())
                        && ProductsExpServiceConstant.BUSINESS_HR_CLOSE.equals(fundRuleInfoList.getFundAllowOtx()));
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
        return false;
    }

    /**
     * Method isCASADormant get Customer account and check dormant status
     *
     * @param correlationId
     * @param ffsRequestBody
     */
    public boolean isCASADormant(String correlationId, FfsRequestBody ffsRequestBody) {
        String responseCustomerExp = null;
        try {
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
            responseCustomerExp = accountRequestClient.callCustomerExpService(invHeaderReqParameter, ffsRequestBody.getCrmId());
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCustomerExp);
            return UtilMap.isCASADormant(responseCustomerExp);
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
    }

    /**
     * Method isSuitabilityExpired Call MF service to check suitability is expire.
     *
     * @param correlationId
     * @param ffsRequestBody
     */
    public boolean isSuitabilityExpired(String correlationId, FfsRequestBody ffsRequestBody) {
        ResponseEntity<TmbOneServiceResponse<SuitabilityInfo>> responseResponseEntity = null;
        try {
            SuitabilityBody suitabilityBody = new SuitabilityBody();
            suitabilityBody.setRmNumber(ffsRequestBody.getCrmId());
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
            responseResponseEntity = investmentRequestClient.callInvestmentFundSuitabilityService(invHeaderReqParameter, suitabilityBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseResponseEntity);
            return UtilMap.isSuitabilityExpire(responseResponseEntity.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
    }

    /**
     * Method isCustIDExpired call to customer-info and get id_expire_date to verify with current date
     *
     * @param ffsRequestBody
     */
    @LogAround
    public boolean isCustIDExpired(FfsRequestBody ffsRequestBody) {
        CompletableFuture<CustGeneralProfileResponse> responseResponseEntity = null;
        try {
            responseResponseEntity = productExpAsyncService.fetchCustomerProfile(ffsRequestBody.getCrmId());
            CompletableFuture.allOf(responseResponseEntity);
            CustGeneralProfileResponse responseData = responseResponseEntity.get();
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseData);
            return UtilMap.isCustIDExpired(responseData);
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
    }

    /**
     * Generic Method to call MF Service getFundList
     *
     * @param fundListRequest
     * @param correlationId
     * @return List<FundClassListInfo>
     */
    @LogAround
    public List<FundClassListInfo> getFundList(String correlationId, FundListRequest fundListRequest) {
        Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
        List<FundClassListInfo> listFund = new ArrayList<>();
        try {
            UnitHolder unitHolder = new UnitHolder();
            String unitHolderList = fundListRequest.getUnitHolderNumber().stream().collect(Collectors.joining(","));
            unitHolder.setUnitHolderNo(unitHolderList);

            CompletableFuture<List<FundClassListInfo>> fetchFundListInfo =
                    productExpAsyncService.fetchFundListInfo(invHeaderReqParameter, correlationId, ProductsExpServiceConstant.INVESTMENT_CACHE_KEY);
            CompletableFuture<FundSummaryResponse> fetchFundSummary = productExpAsyncService.fetchFundSummary(invHeaderReqParameter, unitHolder);
            CompletableFuture<List<CustomerFavoriteFundData>> fetchFundFavorite = productExpAsyncService.fetchFundFavorite(invHeaderReqParameter, fundListRequest.getCrmId());

            CompletableFuture.allOf(fetchFundListInfo, fetchFundSummary, fetchFundFavorite);
            listFund = fetchFundListInfo.get();
            FundSummaryResponse fundSummaryResponse = fetchFundSummary.get();
            List<CustomerFavoriteFundData> customerFavoriteFundDataList = fetchFundFavorite.get();
            listFund = UtilMap.mappingFollowingFlag(listFund, customerFavoriteFundDataList);
            listFund = UtilMap.mappingBoughtFlag(listFund, fundSummaryResponse);
            return listFund;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return listFund;
        }
    }

    @LogAround
    public SuggestAllocationDTO getSuggestAllocation(String correlationId, String crmID) {
        UnitHolder unitHolder = new UnitHolder();
        Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
        try {
            List<String> portList = getPortListForFundSummary(invHeaderReqParameter, crmID);
            unitHolder.setUnitHolderNo(portList.stream().map(String::valueOf).collect(Collectors.joining(",")));
            CompletableFuture<FundSummaryResponse> fundSummary = productExpAsyncService.fetchFundSummary(invHeaderReqParameter, unitHolder);
            CompletableFuture<SuitabilityInfo> suitabilityInfo = productExpAsyncService.fetchSuitabilityInquiry(invHeaderReqParameter, crmID);
            CompletableFuture.allOf(fundSummary, suitabilityInfo);
            String suitabilityScore = suitabilityInfo.get().getSuitabilityScore();
            ResponseEntity<TmbOneServiceResponse<FundAllocationResponse>> fundAllocationResponse = investmentRequestClient.callInvestmentFundAllocation(invHeaderReqParameter, FundAllocationRequestBody.builder().suitabilityScore(suitabilityScore).build());
            return mappingSuggestAllocationDto(fundSummary.get().getBody().getFundClassList().getFundClass(), fundAllocationResponse.getBody().getData());
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return null;
        }
    }

    private List<String> getPortListForFundSummary(Map<String, String> invHeaderReqParameter, String crmID) throws com.fasterxml.jackson.core.JsonProcessingException {
        List<String> portList = new ArrayList<>();
        String portListStr = accountRequestClient.getPortList(invHeaderReqParameter, crmID);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readValue(portListStr, JsonNode.class);
        JsonNode dataNode = node.get("data");
        ArrayNode arrayNode = (ArrayNode) dataNode.get("mutual_fund_accounts");
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode itr = arrayNode.get(i);
            portList.add(itr.get("acct_nbr").textValue());
        }
        return portList;
    }

    private SuggestAllocationDTO mappingSuggestAllocationDto(List<FundClass> fundClass, FundAllocationResponse fundAllocationResponse) {
        List<MutualFundWithFundSuggestedAllocation> mutualFundWithFundSuggestedAllocations = mergeMutualFundWithSuggestAllocation(fundClass, fundAllocationResponse);
        return SuggestAllocationDTO.builder()
                .mutualFund(
                        fundClass.stream()
                                .filter(f -> !f.getFundClassCode().equals("090"))
                                .map(f -> MutualFund.builder()
                                        .fundClassCode(f.getFundClassCode())
                                        .fundClassNameEN(f.getFundClassNameEN())
                                        .fundClassNameTH(f.getFundClassNameTH())
                                        .fundClassPercent(f.getFundClassPercent())
                                        .build())
                                .collect(Collectors.toList()))
                .fundSuggestedAllocation(FundSuggestedAllocation.builder()
                        .modelNumber(fundAllocationResponse.getModelNumber())
                        .suitabilityScore(fundAllocationResponse.getSuitabilityScore())

                        .fundSuggestionList(fundAllocationResponse.getFundSuggestAllocationList().stream()
                                .map(fl -> FundSuggestion.builder()
                                        .fundClassCode(fl.getFundClassCode())
                                        .fundClassNameEn(fl.getFundClassNameEn())
                                        .fundClassNameTh(fl.getFundClassNameTh())
                                        .recommendedPercent(fl.getRecommendedPercent())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .mutualFundWithFundSuggestedAllocation(mutualFundWithFundSuggestedAllocations)
                .build();
    }

    private List<MutualFundWithFundSuggestedAllocation> mergeMutualFundWithSuggestAllocation(List<FundClass> fundClass, FundAllocationResponse fundAllocationResponse) {
        List<MutualFundWithFundSuggestedAllocation> mutualFundWithFundSuggestedAllocationList = new ArrayList<>();
        ArrayList<String> matchClassCode = new ArrayList<>();
        for (FundClass mutualFund : fundClass) {
            if (mutualFund.getFundClassCode().equals("090")) {
                continue;
            }
            boolean isNotMatchMutualFundAndSuggestAllocation = true;
            for (FundSuggestAllocationList suggestFundList : fundAllocationResponse.getFundSuggestAllocationList()) {
                if (mutualFund.getFundClassCode().equals(suggestFundList.getFundClassCode())
                ) {
                    matchClassCode.add(mutualFund.getFundClassCode());
                    mutualFundWithFundSuggestedAllocationList.add(MutualFundWithFundSuggestedAllocation.builder()
                            .fundClassCode(mutualFund.getFundClassCode())
                            .fundClassNameTh(mutualFund.getFundClassNameTH())
                            .fundClassNameEn(mutualFund.getFundClassNameEN())
                            .fundClassPercent(mutualFund.getFundClassPercent())
                            .recommendedPercent(suggestFundList.getRecommendedPercent())
                            .fundSuggestionList(suggestFundList.getFundList().stream()
                                    .map(fl -> SubFundSuggestion.builder()
                                            .fundShortName(fl.getFundShortName())
                                            .fundCode(fl.getFundCode())
                                            .fundPercent(fl.getFundPercent())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build()
                    );
                    isNotMatchMutualFundAndSuggestAllocation = false;
                }
            }
            if (isNotMatchMutualFundAndSuggestAllocation) {
                mutualFundWithFundSuggestedAllocationList.add(MutualFundWithFundSuggestedAllocation.builder()
                        .fundClassCode(mutualFund.getFundClassCode())
                        .fundClassNameTh(mutualFund.getFundClassNameTH())
                        .fundClassNameEn(mutualFund.getFundClassNameEN())
                        .fundClassPercent(mutualFund.getFundClassPercent())
                        .recommendedPercent("-")
                        .fundSuggestionList(null)
                        .build()
                );
            }
        }

        mutualFundWithFundSuggestedAllocationList.addAll(fundAllocationResponse.getFundSuggestAllocationList().stream()
                .filter(fl -> !matchClassCode.contains(fl.getFundClassCode()))
                .map(fl -> MutualFundWithFundSuggestedAllocation.builder()
                        .fundClassCode(fl.getFundClassCode())
                        .fundClassNameEn(fl.getFundClassNameEn())
                        .fundClassNameTh(fl.getFundClassNameTh())
                        .fundClassPercent("-")
                        .recommendedPercent(fl.getRecommendedPercent())
                        .fundSuggestionList(fl.getFundList().stream()
                                .map(fle -> SubFundSuggestion.builder()
                                        .fundShortName(fle.getFundShortName())
                                        .fundCode(fle.getFundCode())
                                        .fundPercent(fle.getFundPercent())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList()));
        return mutualFundWithFundSuggestedAllocationList;
    }

    /**
     * Method constructActivityLogDataForBuyHoldingFund
     *
     * @param correlationId
     * @param activityType
     * @param trackingStatus
     * @param alternativeRequest
     */
    public ActivityLogs constructActivityLogDataForBuyHoldingFund(String correlationId,
                                                                  String activityType,
                                                                  String trackingStatus,
                                                                  AlternativeRequest alternativeRequest) {
        String failReason = alternativeRequest.getProcessFlag().equals(ProductsExpServiceConstant.PROCESS_FLAG_Y) ?
                ProductsExpServiceConstant.SUCCESS_MESSAGE : ProductsExpServiceConstant.FAILED_MESSAGE;

        ActivityLogs activityData = new ActivityLogs(correlationId, String.valueOf(System.currentTimeMillis()), trackingStatus);
        activityData.setActivityStatus(failReason);
        activityData.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_CHANNEL);
        activityData.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_APP_VERSION);
        activityData.setFailReason(failReason);
        activityData.setActivityType(activityType);
        activityData.setCrmId(alternativeRequest.getCrmId());
        activityData.setVerifyFlag(alternativeRequest.getProcessFlag());
        activityData.setReason(failReason);
        activityData.setFundCode(alternativeRequest.getFundCode());
        activityData.setFundClass(alternativeRequest.getFundClassThaiHubName());
        if (!StringUtils.isEmpty(alternativeRequest.getUnitHolderNumber())) {
            activityData.setUnitHolderNo(alternativeRequest.getUnitHolderNumber());
        } else {
            activityData.setUnitHolderNo(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_UNIT_HOLDER);
        }
        return activityData;
    }

    /**
     * Method logactivity
     * suitabilityScore
     *
     * @param data
     */
    @Async
    @LogAround
    public void logActivity(ActivityLogs data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String output = mapper.writeValueAsString(data);
            logger.info("Activity Data request is  {} : ", output);
            logger.info("Activity Data request topicName is  {} : ", topicName);
            kafkaProducerService.sendMessageAsync(topicName, output);
            logger.info("callPostEventService -  data posted to activity_service : {}", System.currentTimeMillis());
        } catch (Exception e) {
            logger.info("Unable to log the activity request : {}", e.toString());
        }
    }
}