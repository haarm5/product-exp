package com.tmb.oneapp.productsexpservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.customer.UpdateEStatmentRequest;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;

@Service
public class ApplyEStatementService {

	private static final TMBLogger<ApplyEStatementService> logger = new TMBLogger<>(ApplyEStatementService.class);
	private CustomerServiceClient customerServiceClient;
	private CreditCardClient creditCardClient;

	public ApplyEStatementService(CustomerServiceClient customerServiceClient, CreditCardClient creditCardClient) {
		this.customerServiceClient = customerServiceClient;
		this.creditCardClient = creditCardClient;
	}

	/**
	 * get e-statement
	 * 
	 * @param correlationId
	 * @param crmId
	 * @return
	 */
	public ApplyEStatementResponse getEStatement(String crmId, String correlationId) {
		ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> result = customerServiceClient
				.getCustomerEStatement(crmId, correlationId);
		return result.getBody().getData();
	}

	/**
	 * Update e statement by crm id
	 * 
	 * @param crmId
	 * @param correlationId
	 * @param updateEstatementReq
	 */
	public void updateEstatement(String crmId, String correlationId, UpdateEStatmentRequest updateEstatementReq) {
		ApplyEStatementResponse currentEstatementResponse = getEStatement(crmId, correlationId);
		if ("Y".equals(currentEstatementResponse.getCustomer().getStatementFlag().getECreditcardStatementFlag())) {
			logger.info("This rm already apply e statment completed");
			return;
		}
		updateEStatementOnSilverLake(crmId, correlationId, updateEstatementReq);

	}
	
	/**
	 * Update e statment on silverlake
	 * @param crmId
	 * @param correlationId
	 * @param updateEstatementReq
	 */
	private void updateEStatementOnSilverLake(String crmId, String correlationId,
			UpdateEStatmentRequest updateEstatementReq) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
		headers.put(ProductsExpServiceConstant.X_CRMID, crmId);
		creditCardClient.updateEmailEStatement(headers, updateEstatementReq);
		creditCardClient.updateEnableEStatement(headers, updateEstatementReq);
	}

}
