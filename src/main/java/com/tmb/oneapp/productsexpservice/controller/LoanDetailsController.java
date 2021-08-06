package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardEmail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.EStatementDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;
import com.tmb.oneapp.productsexpservice.model.loan.AccountId;
import com.tmb.oneapp.productsexpservice.model.loan.HomeLoanFullInfoResponse;
import com.tmb.oneapp.productsexpservice.model.loan.Payment;
import com.tmb.oneapp.productsexpservice.model.loan.Rates;
import com.tmb.oneapp.productsexpservice.service.ApplyEStatementService;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.util.ConversionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "Credit Card-Cash For You")
public class LoanDetailsController {
	private static final TMBLogger<LoanDetailsController> log = new TMBLogger<>(LoanDetailsController.class);
	private final AccountRequestClient accountRequestClient;
	private final CommonServiceClient commonServiceClient;
	private final CreditCardLogService creditCardLogService;
	private final CustomerServiceClient customerServiceClient;
	private final ApplyEStatementService applyEStatementService;

	/**
	 * Constructor
	 *
	 * @param accountRequestClient
	 * @param commonServiceClient
	 * @param creditCardLogService
	 */
	@Autowired
	public LoanDetailsController(AccountRequestClient accountRequestClient, CommonServiceClient commonServiceClient,
			CreditCardLogService creditCardLogService, CustomerServiceClient customerServiceClient,
			ApplyEStatementService applyEStatementService) {
		this.accountRequestClient = accountRequestClient;
		this.commonServiceClient = commonServiceClient;
		this.creditCardLogService = creditCardLogService;
		this.customerServiceClient = customerServiceClient;
		this.applyEStatementService = applyEStatementService;
	}

	/**
	 * @param requestHeadersParameter
	 * @param requestBody
	 * @return
	 */
	@LogAround
	@PostMapping(value = "/loan/get-account-detail", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "get loan account detail")
	@ApiImplicitParams({
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> getLoanAccountDetail(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeadersParameter,
			@ApiParam(value = "Account ID", defaultValue = "00016109738001", required = true) @RequestBody AccountId requestBody) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<HomeLoanFullInfoResponse> oneServiceResponse = new TmbOneServiceResponse<>();

		String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);
		String activityDate = Long.toString(System.currentTimeMillis());
		String activityId = ProductsExpServiceConstant.ACTIVITY_ID_VIEW_LOAN_LENDING_SCREEN;
		CreditCardEvent creditCardEvent = new CreditCardEvent(
				requestHeadersParameter.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID.toLowerCase()), activityDate,
				activityId);

		try {

			String accountId = requestBody.getAccountNo();
			if (!Strings.isNullOrEmpty(accountId)) {
				ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> loanResponse = accountRequestClient
						.getLoanAccountDetail(correlationId, requestBody);
				int statusCodeValue = loanResponse.getStatusCodeValue();
				HttpStatus statusCode = loanResponse.getStatusCode();

				if (loanResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

					return getTmbOneServiceResponseResponseEntity(requestHeadersParameter, responseHeaders,
							oneServiceResponse, crmId, correlationId, creditCardEvent, loanResponse);
				} else {
					return getFailedResponse(responseHeaders, oneServiceResponse);

				}
			} else {
				return getFailedResponse(responseHeaders, oneServiceResponse);
			}

		} catch (Exception e) {
			log.error("Error while getLoanAccountDetails: {}", e);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService()));
			return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
		}

	}

	ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> getTmbOneServiceResponseResponseEntity(
			Map<String, String> requestHeadersParameter, HttpHeaders responseHeaders,
			TmbOneServiceResponse<HomeLoanFullInfoResponse> oneServiceResponse, String crmId, String correlationId,
			CreditCardEvent creditCardEvent,
			ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> loanResponse) {
		HomeLoanFullInfoResponse loanDetails = loanResponse.getBody().getData();
		String productId = loanResponse.getBody().getData().getAccount().getProductId();
		Rates rates = loanResponse.getBody().getData().getAccount().getRates();
		Double currentInterestRate = ConversionUtil.stringToDouble(rates.getCurrentInterestRate());
		Double originalInterestRate = ConversionUtil.stringToDouble(rates.getOriginalInterestRate());
		String monthlyPaymentAmount = loanDetails.getAccount().getPayment().getMonthlyPaymentAmount();
		Double monthlyPayment = ConversionUtil.stringToDouble(monthlyPaymentAmount);
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		Payment payment = loanDetails.getAccount().getPayment();

		String formattedPayment = df.format(monthlyPayment);
		payment.setMonthlyPaymentAmount(formattedPayment);
		DecimalFormat threeDecimalPlaces = new DecimalFormat("#.00");
		String currentInterest = threeDecimalPlaces.format(currentInterestRate);
		String originalInterest = threeDecimalPlaces.format(originalInterestRate);
		String currentInterestRateInPercent = currentInterest.concat(" %");
		String originalInterestRateInPercent = originalInterest.concat(" %");
		rates.setCurrentInterestRate(currentInterestRateInPercent);
		rates.setOriginalInterestRate(originalInterestRateInPercent);
		ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> fetchProductConfigList = commonServiceClient
				.getProductConfig(correlationId);

		List<ProductConfig> list = fetchProductConfigList.getBody().getData();
		Iterator<ProductConfig> iterator = list.iterator();
		while (iterator.hasNext()) {
			ProductConfig productConfig = iterator.next();
			if (productConfig.getProductCode().equalsIgnoreCase(productId)) {
				loanDetails.setProductConfig(productConfig);
			}
		}
		processSetEStatementDetail(loanDetails, crmId, correlationId);
		processSetAccountID(loanDetails);
		/* Activity log */
		creditCardEvent = creditCardLogService.viewLoanLandingScreenEvent(creditCardEvent, requestHeadersParameter,
				loanDetails);
		creditCardLogService.logActivity(creditCardEvent);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		oneServiceResponse.setData(loanDetails);
		return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
	}

	private void processSetAccountID(HomeLoanFullInfoResponse loanDetails) {
		if (loanDetails.getAccount() != null) {
			if (loanDetails.getAccount().getDirectDebit() != null) {
				if (!"01".equals(loanDetails.getAccount().getDirectDebit().getAffiliateSequenceNo())
						&& !"1".equals(loanDetails.getAccount().getDirectDebit().getSequenceNo())) {
					loanDetails.getAccount().getDirectDebit().setAccountId("");
				}
			}
		}
	}

	private void processSetEStatementDetail(HomeLoanFullInfoResponse loanDetails, String crmId,
			String correlationId) {
		EStatementDetail result = new EStatementDetail();
		CardEmail cardEmail = new CardEmail();
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> responseWorkingProfileInfo = customerServiceClient
				.getCustomerProfile(crmId);
		CustGeneralProfileResponse profileResponse = responseWorkingProfileInfo.getBody().getData();
		if (profileResponse != null) {
			result.setEmailAddress(profileResponse.getEmailAddress());
			result.setEmailVerifyFlag(profileResponse.getEmailVerifyFlag());
			cardEmail.setEmailAddress(profileResponse.getEmailAddress());
		}
		ApplyEStatementResponse applyEStatementResponse = applyEStatementService.getEStatement(crmId, correlationId);
		if (applyEStatementResponse != null) {
			cardEmail.setEmaileStatementFlag(
					applyEStatementResponse.getCustomer().getStatementFlag().getECashToGoStatementFlag());
		}
		loanDetails.setCardEmail(cardEmail);
		loanDetails.setEstatementDetail(result);
	}

	ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> getFailedResponse(HttpHeaders responseHeaders,
			TmbOneServiceResponse<HomeLoanFullInfoResponse> oneServiceResponse) {
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
	}
}
