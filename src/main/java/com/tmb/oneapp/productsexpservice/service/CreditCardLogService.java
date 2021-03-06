package com.tmb.oneapp.productsexpservice.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.creditcard.CardInstallment;
import com.tmb.common.model.creditcard.CardInstallmentResponse;
import com.tmb.common.model.customer.UpdateEStatmentRequest;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.InstallmentPlan;
import com.tmb.oneapp.productsexpservice.model.loan.HomeLoanFullInfoResponse;
import com.tmb.oneapp.productsexpservice.util.ConversionUtil;

@Service
public class CreditCardLogService {
	private static final TMBLogger<CreditCardLogService> logger = new TMBLogger<>(CreditCardLogService.class);
	@Value("${com.tmb.oneapp.service.activity.topic.name}")
	private String topicName = "activity";
	private KafkaProducerService kafkaProducerService;
	private CreditCardClient creditCardClient;
	private CommonServiceClient commonServiceClient;
	private SimpleDateFormat respnseformatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat activifyFormatter = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * constructor
	 *
	 * @param topicName
	 * @param kafkaProducerService
	 */
	public CreditCardLogService(KafkaProducerService kafkaProducerService, CreditCardClient creditCardClient,
			CommonServiceClient commonServiceClient) {
		this.kafkaProducerService = kafkaProducerService;
		this.creditCardClient = creditCardClient;
		this.commonServiceClient = commonServiceClient;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @return
	 */
	public CreditCardEvent callActivityBaseEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {

		logger.info("Inside callActivityBaseEvent");

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(reqHeader.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		creditCardEvent.setResult(ProductsExpServiceConstant.SUCCESS);
		return creditCardEvent;
	}

	/**
	 * Call activity logs for verifyCvv Event
	 *
	 * @param creditCardEvent
	 * @param requestHeadersParameter
	 * @return
	 */
	public CreditCardEvent onClickNextButtonEvent(CreditCardEvent creditCardEvent,
			Map<String, String> requestHeadersParameter, SetCreditLimitReq requestBody) {

		populateBaseEvents(creditCardEvent, requestHeadersParameter);
		creditCardEvent
				.setCardNumber(requestHeadersParameter.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		creditCardEvent.setCurrentLimit(requestBody.getCurrentCreditLimit());
		creditCardEvent.setNewLimit((requestBody.getPreviousCreditLimit()));
		creditCardEvent.setType(ProductsExpServiceConstant.CHANGE_TYPE_PERMANENT);
		return creditCardEvent;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param requestBody
	 * @param mode
	 * @return
	 */
	public CreditCardEvent onClickNextButtonLimitEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
			SetCreditLimitReq requestBody, String mode) {

		if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_PERMANENT)) {
			creditCardEvent.setCardNumber("xx" + requestBody.getAccountId().substring(21, 25));
			creditCardEvent.setNewLimit(formateForCurrency(requestBody.getCurrentCreditLimit()));
			creditCardEvent.setCurrentLimit(formateForCurrency(requestBody.getPreviousCreditLimit()));
			creditCardEvent.setType("Adjust Permanent Limit");

		} else if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_TEMPORARY)) {
			creditCardEvent.setCardNumber("xx" + requestBody.getAccountId().substring(21, 25));
			creditCardEvent.setExpiryDateForTempRequest(requestBody.getExpiryDate());
			creditCardEvent.setReasonForRequest(requestBody.getReasonDescEn());
			creditCardEvent.setType("Request Temporary Limit");
		}
		populateBaseEvents(creditCardEvent, reqHeader);

		return creditCardEvent;
	}

	/**
	 * 
	 * @param moneyString
	 * @return
	 */
	private String formateForCurrency(String moneyString) {

		try {
			BigDecimal money = new BigDecimal(moneyString);
			return String.format("%,.2f", money);
		} catch (Exception e) {
			logger.error("Invalid money input " + moneyString);
		}

		return null;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param requestBody
	 * @return
	 */
	public CreditCardEvent completeUsageListEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
			SetCreditLimitReq requestBody, String result) {

		populateBaseEvents(creditCardEvent, reqHeader);
		creditCardEvent.setCardNumber("xx" + requestBody.getAccountId().substring(21, 25));
		creditCardEvent.setResult(result);
		return creditCardEvent;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @return
	 */
	public CreditCardEvent onVerifyPinEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(reqHeader.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		creditCardEvent.setResult(ProductsExpServiceConstant.SUCCESS);
		return creditCardEvent;
	}

	/**
	 * Call activity logs for verifyCvv Event
	 *
	 * @param creditCardEvent
	 * @param reqHeader
	 * @return
	 */
	public CreditCardEvent verifyCvvBaseEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(reqHeader.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		return creditCardEvent;
	}

	/**
	 * @param correlationId
	 * @param reqHeader
	 * @param requestBody
	 * @param data
	 */
	public void generateApplySoGoodConfirmEvent(String correlationId, Map<String, String> reqHeader,
			List<CardInstallmentResponse> data) {

		if (CollectionUtils.isNotEmpty(data)) {
			for (CardInstallmentResponse response : data) {
				constructCardEvent(correlationId, reqHeader, response);
			}
		}

	}

	/**
	 * Construct installment confirm event in activity log
	 * 
	 * @param correlationId
	 * @param reqHeader
	 * @param e
	 */
	private void constructCardEvent(String correlationId, Map<String, String> reqHeader, CardInstallmentResponse e) {
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()),
				ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON);

		creditCardEvent.setCardNumber("xx" + e.getCreditCard().getAccountId().substring(21, 25));
		creditCardEvent.setTransactionDescription(e.getCreditCard().getCardInstallment().getTransactionDescription());
		creditCardEvent.setTransactionDate(formateTransactionDate(e.getCreditCard().getCardInstallment()));

		populateBaseEvents(creditCardEvent, reqHeader);

		CardInstallment cardInstallment = e.getCreditCard().getCardInstallment();
		creditCardEvent.setPlan(converPlan(cardInstallment, correlationId));
		creditCardEvent.setTransactionDescription(cardInstallment.getTransactionDescription());
		creditCardEvent.setInitailVector(e.getInitialVector());

		Double amountInDouble = ConversionUtil.stringToDouble(cardInstallment.getAmounts());
		Double installmentInDouble = ConversionUtil.stringToDouble(cardInstallment.getMonthlyInstallments());

		Double interestInDouble = ConversionUtil.stringToDouble(cardInstallment.getInterest());
		Double amountPlusTotalInterest = amountInDouble + interestInDouble;

		creditCardEvent.setAmountMonthlyInstallment(formateForCurrency(amountInDouble.toString()) + "+"
				+ formateForCurrency(installmentInDouble.toString()));

		creditCardEvent.setTotalAmountTotalIntrest(formateForCurrency(interestInDouble.toString()) + "+"
				+ formateForCurrency(amountPlusTotalInterest.toString()));

		if (Objects.nonNull(e.getStatus()) && "0".equals(e.getStatus().getStatusCode())) {
			creditCardEvent.setResult(ProductsExpServiceConstant.SUCCESS);
			creditCardEvent.setActivityStatus(ProductsExpServiceConstant.SUCCESS);

		} else {
			creditCardEvent.setResult(ProductsExpServiceConstant.FAILED);
			creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE_ACT_LOG);
			if (!e.getStatus().getErrorStatus().isEmpty()) {
				creditCardEvent.setFailReason(e.getStatus().getErrorStatus().get(0).getDescription());
			}
		}
		logActivity(creditCardEvent);

	}
	
	/**
	 * Re format post date information
	 * @param cardInstallment
	 * @return
	 */
	private String formateTransactionDate(CardInstallment cardInstallment) {
		String formatePostedDate = null;

		if (StringUtils.isNotEmpty(cardInstallment.getPostDate())) {
			try {
				Date postDated = respnseformatter.parse(cardInstallment.getPostDate());
				formatePostedDate = activifyFormatter.format(postDated);
			} catch (ParseException e) {
				logger.error(e.toString(),e);
			}
		}
		return formatePostedDate;
	}

	/**
	 * Generate format plan
	 * 
	 * @param cardInstallment
	 * @param correlationId
	 * @return
	 */
	private String converPlan(CardInstallment cardInstallment, String correlationId) {
		StringBuffer bf = new StringBuffer();
		ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> responseInstallments = creditCardClient
				.getInstallmentPlan(correlationId);
		List<InstallmentPlan> installmentPlans = responseInstallments.getBody().getData();
		InstallmentPlan reqInstallmentPlan = null;
		for (InstallmentPlan installmentplan : installmentPlans) {
			if (installmentplan.getInstallmentsPlan().equals(cardInstallment.getPromotionModelNo())) {
				reqInstallmentPlan = installmentplan;
			}
		}
		if (Objects.nonNull(reqInstallmentPlan)) {
			bf.append(formateForCurrency(reqInstallmentPlan.getInterestRate()) + "%");
			bf.append(StringUtils.SPACE);
			bf.append(reqInstallmentPlan.getPaymentTerm());
			bf.append(StringUtils.SPACE);
			bf.append("Months");
		}

		return bf.toString();
	}

	/**
	 * method for populating base events for Activity logs
	 *
	 * @param creditCardEvent
	 * @param reqHeader
	 */
	private void populateBaseEvents(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {
		creditCardEvent.setIpAddress(reqHeader.get(ProductsExpServiceConstant.X_FORWARD_FOR));
		creditCardEvent.setOsVersion(reqHeader.get(ProductsExpServiceConstant.OS_VERSION));
		creditCardEvent.setChannel(reqHeader.get(ProductsExpServiceConstant.CHANNEL));
		creditCardEvent.setAppVersion(reqHeader.get(ProductsExpServiceConstant.APP_VERSION));
		creditCardEvent.setCrmId(reqHeader.get(ProductsExpServiceConstant.X_CRMID));
		creditCardEvent.setDeviceId(reqHeader.get(ProductsExpServiceConstant.DEVICE_ID));
		creditCardEvent.setDeviceModel(reqHeader.get(ProductsExpServiceConstant.DEVICE_MODEL));
		creditCardEvent
				.setCorrelationId(reqHeader.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID.toLowerCase()));
		creditCardEvent.setActivityStatus(ProductsExpServiceConstant.SUCCESS);
	}

	/**
	 * Method sending data to Kafka producer
	 *
	 * @param loginEvent
	 */
	@Async
	@LogAround
	public void logActivity(CreditCardEvent loginEvent) {
		try {
			String output = TMBUtils.convertJavaObjectToString(loginEvent);
			logger.info("############  Current Date : {}", loginEvent.getActivityDate());
			logger.info("Activity Log Payload : {}", output);
			kafkaProducerService.sendMessageAsync(topicName, output);
			logger.info("callPostEventService -  data posted to event_service : {}", System.currentTimeMillis());
		} catch (Exception e) {
			logger.info("Unable to process the request : {}", e);
		}
	}

	/**
	 * Activity log for finish block card
	 *
	 * @param status
	 * @param activityId
	 * @param correlationId
	 * @param activityDate
	 * @param accountId
	 */
	@Async
	@LogAround
	public void finishBlockCardActivityLog(String status, String correlationId, String accountId, String failReason,
			Map<String, String> reqHeader, String iv) {
		String activityId = ProductsExpServiceConstant.FINISH_BLOCK_CARD_ACTIVITY_ID;
		String activityDate = Long.toString(System.currentTimeMillis());
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate, activityId);
		if (status.equalsIgnoreCase(ProductsExpServiceConstant.FAILURE_ACT_LOG)) {
			creditCardEvent.setFailReason(failReason);
		}
		populateBaseEvents(creditCardEvent, reqHeader);
		creditCardEvent.setCardNumber("xx" + accountId.substring(21, 25));
		creditCardEvent.setActivityStatus(status);
		creditCardEvent.setInitailVector(iv);
		creditCardEvent.setResult(status);
		logActivity(creditCardEvent);
	}

	/**
	 * @param status
	 * @param activityId
	 * @param correlationId
	 * @param activityDate
	 * @param accountId
	 * @param failReason
	 * @param iv
	 */
	@Async
	@LogAround
	public void finishSetPinActivityLog(String status, String correlationId, String accountId, String failReason,
			Map<String, String> reqHeader, String iv) {
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()),
				ProductsExpServiceConstant.SET_PIN_ACTIVITY_LOG);
		if (status.equalsIgnoreCase(ProductsExpServiceConstant.FAILURE_ACT_LOG)) {
			creditCardEvent.setFailReason(failReason);
		}
		populateBaseEvents(creditCardEvent, reqHeader);
		creditCardEvent.setResult(status);
		creditCardEvent.setCardNumber("xx" + accountId.substring(21, 25));
		creditCardEvent.setActivityStatus(status);
		creditCardEvent.setInitailVector(iv);
		logActivity(creditCardEvent);
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param response
	 * @return
	 */
	public CreditCardEvent viewLoanLandingScreenEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
			HomeLoanFullInfoResponse response) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setLoanNumber(response.getAccount().getId().substring(1, 11));
		creditCardEvent.setProductName(response.getProductConfig().getProductNameEN());
		return creditCardEvent;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param fetchCardResponse
	 * @return
	 */
	public CreditCardEvent loadCardDetailsEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
			FetchCardResponse fetchCardResponse) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(fetchCardResponse.getCreditCard().getAccountId().substring(21, 25));
		creditCardEvent.setProductName(fetchCardResponse.getProductCodeData().getProductNameEN());
		return creditCardEvent;
	}

	/**
	 * Create event for update estatment
	 * 
	 * @param requestHeaders
	 * @param productName
	 * @param cardNo
	 * @param object
	 * @param errorCode
	 * @return
	 */
	public CreditCardEvent generateEStatementEvent(Map<String, String> requestHeaders, String productName,
			String cardNo, boolean success, String errorCode) {
		String correlationId = requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String result = success ? ProductsExpServiceConstant.SUCCESS : ProductsExpServiceConstant.FAILURE_ACT_LOG;
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()),
				ProductsExpServiceConstant.ESTAMENT_CONFIRM_CARD);
		creditCardEvent.setActivityStatus(result);
		creditCardEvent.setProductName(productName);
		creditCardEvent.setCardNumber(cardNo);
		creditCardEvent.setReasonForRequest(errorCode);
		return creditCardEvent;
	}

	/**
	 * Update Estatement for card type
	 * 
	 * @param requestHeaders
	 * @param updateEstatementReq
	 * @param result
	 * @param errorCode
	 * @param iv
	 */
	public void updatedEStatmentCard(Map<String, String> requestHeaders, UpdateEStatmentRequest updateEstatementReq,
			boolean result, String errorCode, String iv) {
		String correlationId = requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String productName = constructProductNameInfomation(correlationId, updateEstatementReq);
		ResponseEntity<FetchCardResponse> fetchCardInfoResp = creditCardClient.getCreditCardDetails(correlationId,
				updateEstatementReq.getAccountId());
		String cardNo = "xx" + fetchCardInfoResp.getBody().getCreditCard().getAccountId().substring(21, 25);
		CreditCardEvent updateEStatmentEvent = generateEStatementEvent(requestHeaders, productName, cardNo, result,
				errorCode);
		constructCommonDetail(requestHeaders, updateEStatmentEvent);
		updateEStatmentEvent.setReasonForRequest(errorCode);
		updateEStatmentEvent.setInitailVector(iv);
		updateEStatmentEvent.setResult(result ? ProductsExpServiceConstant.SUCCESS : ProductsExpServiceConstant.FAILED);
		logActivity(updateEStatmentEvent);

	}

	/**
	 * Construct product name
	 * 
	 * @param correlationId
	 * @param updateEstatementReq
	 * @return
	 */
	private String constructProductNameInfomation(String correlationId, UpdateEStatmentRequest updateEstatementReq) {

		String productCodeName = "";

		ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = commonServiceClient
				.getProductConfig(correlationId);

		List<ProductConfig> productConfigs = response.getBody().getData();

		if (StringUtils.isNotEmpty(updateEstatementReq.getAccountId())) {
			ResponseEntity<FetchCardResponse> fetchCardRes = creditCardClient.getCreditCardDetails(correlationId,
					updateEstatementReq.getAccountId());
			String productCode = fetchCardRes.getBody().getCreditCard().getProductId();
			for (ProductConfig productInfo : productConfigs) {
				if (StringUtils.isNoneBlank(productCode) && productCode.equals(productInfo.getProductCode())) {
					productCodeName = "(" + productInfo.getProductCode() + ")  " + productInfo.getProductNameEN();
				}
			}
		} else {
			for (ProductConfig productInfo : productConfigs) {
				if (CollectionUtils.isNotEmpty(updateEstatementReq.getProductType())
						&& updateEstatementReq.getProductType().get(0).equals(productInfo.getProductCode())) {

					productCodeName = "(" + productInfo.getProductCode() + ")  " + productInfo.getProductNameEN();
				}
			}
		}

		return productCodeName;

	}

	/**
	 * Update EStatment
	 * 
	 * @param requestHeaders
	 * @param updateEstatementReq
	 * @param result
	 * @param errorCode
	 * @param iv
	 */
	public void updatedEStatmentLoan(Map<String, String> requestHeaders, UpdateEStatmentRequest updateEstatementReq,
			boolean result, String errorCode) {
		String correlationId = requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String productCodeName = constructProductNameInfomation(correlationId, updateEstatementReq);
		CreditCardEvent loanEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()),
				ProductsExpServiceConstant.ESTAMENT_CONFIRM_LOAN);
		constructCommonDetail(requestHeaders, loanEvent);
		loanEvent.setProductName(productCodeName);
		loanEvent.setLoanNumber(updateEstatementReq.getLoanId());
		loanEvent.setReasonForRequest(errorCode);
		loanEvent.setResult(result ? ProductsExpServiceConstant.SUCCESS : ProductsExpServiceConstant.FAILED);

		loanEvent.setActivityStatus(
				result ? ProductsExpServiceConstant.SUCCESS : ProductsExpServiceConstant.FAILURE_ACT_LOG);
		logActivity(loanEvent);

	}

	/**
	 * Construct common detail supported
	 * 
	 * @param requestHeaders
	 */
	private void constructCommonDetail(Map<String, String> requestHeaders, BaseEvent baseEvent) {
		baseEvent.setIpAddress(requestHeaders.get(ProductsExpServiceConstant.X_FORWARD_FOR));
		baseEvent.setOsVersion(requestHeaders.get(ProductsExpServiceConstant.OS_VERSION));
		baseEvent.setChannel(requestHeaders.get(ProductsExpServiceConstant.CHANNEL));
		baseEvent.setAppVersion(requestHeaders.get(ProductsExpServiceConstant.APP_VERSION));
		baseEvent.setCrmId(requestHeaders.get(ProductsExpServiceConstant.X_CRMID));
		baseEvent.setDeviceId(requestHeaders.get(ProductsExpServiceConstant.DEVICE_ID));
		baseEvent.setDeviceModel(requestHeaders.get(ProductsExpServiceConstant.DEVICE_MODEL));
		baseEvent
				.setCorrelationId(requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID.toLowerCase()));

	}

	/**
	 * Create activate card event
	 * 
	 * @param headers
	 * @param iv
	 * @param accountId
	 * @param processStatus
	 * @param errorCode
	 */
	public void processActivateCard(Map<String, String> headers, String iv, String accountId, boolean processStatus,
			String errorCode) {
		String correlationId = headers.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()),
				ProductsExpServiceConstant.ACTIVATED_CARD);
		populateBaseEvents(creditCardEvent, headers);
		String result = processStatus ? ProductsExpServiceConstant.SUCCESS : ProductsExpServiceConstant.FAILURE_ACT_LOG;
		ResponseEntity<FetchCardResponse> fetchCardInfoResp = creditCardClient.getCreditCardDetails(correlationId,
				accountId);
		String cardNo = "xx" + fetchCardInfoResp.getBody().getCreditCard().getAccountId().substring(21, 25);
		creditCardEvent.setActivityStatus(result);
		creditCardEvent.setCardNumber(cardNo);
		creditCardEvent.setInitailVector(iv);
		creditCardEvent.setResult(result);
		if (errorCode != null) {
			creditCardEvent.setFailReason(errorCode);
		}
		logActivity(creditCardEvent);
	}

}