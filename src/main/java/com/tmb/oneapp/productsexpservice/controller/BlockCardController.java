package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.blockcard.BlockCardRequest;
import com.tmb.oneapp.productsexpservice.model.blockcard.BlockCardResponse;
import com.tmb.oneapp.productsexpservice.model.blockcard.Status;
import com.tmb.oneapp.productsexpservice.service.CacheService;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;
import com.tmb.oneapp.productsexpservice.util.InternalRespUtil;

import io.swagger.annotations.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;

/**
 * BlockCardController request mapping will handle apis call and then navigate
 * to respective method
 */
@RestController
@Api(tags = "Block Card Api")
public class BlockCardController {

	private static final TMBLogger<BlockCardController> logger = new TMBLogger<>(BlockCardController.class);
	private final CreditCardClient creditCardClient;
	private final CreditCardLogService creditCardLogService;
	private final NotificationService notificationService;
	private final CacheService cacheService;

	/**
	 * Constructor
	 *
	 * @param creditCardClient
	 * @param creditCardLogService
	 */

	@Autowired
	public BlockCardController(CreditCardClient creditCardClient, CreditCardLogService creditCardLogService,
			NotificationService notificationService, CacheService cacheService) {
		this.creditCardClient = creditCardClient;
		this.creditCardLogService = creditCardLogService;
		this.notificationService = notificationService;
		this.cacheService = cacheService;
	}

	/**
	 * verify block card api
	 *
	 * @param requestBodyParameter
	 * @param requestHeadersParameter
	 * @return block card response
	 */
	@SuppressWarnings("unchecked")
	@LogAround
	@ApiOperation(value = "Block Card Api")
	@PostMapping(value = "/credit-card/block-card")
	@ApiImplicitParams({
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header")

	})
	public ResponseEntity<TmbOneServiceResponse<BlockCardResponse>> blockCardDetails(
			@RequestBody BlockCardRequest requestBodyParameter,
			@ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeadersParameter)
			throws TMBCommonException {
		logger.info("blockCardDetails request body parameter: {}", requestBodyParameter);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<BlockCardResponse> oneServiceResponse = new TmbOneServiceResponse<>();

		String correlationId = requestHeadersParameter
				.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID.toLowerCase());
		String accountId = requestBodyParameter.getAccountId();
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);
		
		if (Strings.isNullOrEmpty(requestHeadersParameter.get(ProductsExpServiceConstant.CHANNEL))) {
			requestHeadersParameter.put(ProductsExpServiceConstant.CHANNEL,
					ProductsExpServiceConstant.CHANNEL_MOBILE_BANKING);
		}
		
		try {
			if (!Strings.isNullOrEmpty(accountId) && !Strings.isNullOrEmpty(correlationId)
					&& accountId.length() == 25) {
				ResponseEntity<com.tmb.common.model.creditcard.BlockCardResponse> blockCardRes = creditCardClient
						.getBlockCardDetails(requestBodyParameter);
				com.tmb.common.model.creditcard.BlockCardResponse blockCardResp = blockCardRes.getBody();

				if (Objects.nonNull(blockCardResp)) {
					String iv = null;

					if (StringUtils.isNotEmpty(blockCardResp.getInitialVector())) {
						iv = blockCardResp.getInitialVector();
					}
					if (blockCardRes.getBody().getStatus().getStatusCode().equals("0")) {
						String txnId = UUID.randomUUID().toString();
						Status status = new Status();
						status.setStatusCode(blockCardRes.getBody().getStatus().getStatusCode());
						status.setDate(Long.toString(System.currentTimeMillis()));
						status.setTxnId(txnId);
						BlockCardResponse res = new BlockCardResponse();
						res.setStatus(status);
						oneServiceResponse.setData(res);
						oneServiceResponse.setStatus(
								new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
										ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
						creditCardLogService.finishBlockCardActivityLog(ProductsExpServiceConstant.SUCCESS,
								correlationId, accountId, "", requestHeadersParameter, iv);
						notificationService.doNotifySuccessForBlockCard(correlationId, accountId, crmId);
						cacheService.removeCacheAfterSuccessCreditCard(correlationId, crmId);
						return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);

					} else {
						creditCardLogService.finishBlockCardActivityLog(ProductsExpServiceConstant.FAILURE_ACT_LOG,
								correlationId, accountId, ProductsExpServiceConstant.INTERNAL_SERVER,
								requestHeadersParameter, iv);
						return (ResponseEntity<TmbOneServiceResponse<BlockCardResponse>>) InternalRespUtil
								.generatedResponseFromService(responseHeaders, oneServiceResponse,
										blockCardResp.getStatus().getErrorStatus());
					}
				} else {
					oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
							ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
					return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
				}

			} else {
				oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
				return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
			}
		} catch (Exception e) {
			creditCardLogService.finishBlockCardActivityLog(ProductsExpServiceConstant.FAILURE_ACT_LOG, correlationId,
					accountId, ProductsExpServiceConstant.INTERNAL_SERVER, requestHeadersParameter, null);
			throw new TMBCommonException(ResponseCode.GENERAL_ERROR.getCode(), ResponseCode.GENERAL_ERROR.getMessage(),
					ResponseCode.GENERAL_ERROR.getService(), HttpStatus.BAD_REQUEST, null);
		}

	}
}
