package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.ProductHoldingsResp;
import com.tmb.oneapp.productsexpservice.model.loan.AccountId;
import com.tmb.oneapp.productsexpservice.model.loan.HomeLoanFullInfoResponse;
import com.tmb.oneapp.productsexpservice.model.loan.LoanStatementRequest;
import com.tmb.oneapp.productsexpservice.model.loan.LoanStatementResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * The interface Account request client.
 */
@FeignClient(name = "${account.service.name}", url = "${account.service.url}")
public interface AccountRequestClient {
	/**
	 * Gets port list.
	 *
	 * @param headers the headers
	 * @param cardId  the card id
	 * @return the port list
	 */
	@GetMapping(value = "${account.service.account.url}", consumes = "application/json", produces = "application/json")
	String getPortList(@RequestHeader Map<String, String> headers, @PathVariable("CRM_ID") String cardId);

	/**
	 * Call investment fund summary service fund summary response.
	 *
	 * @param headers the headers
	 * @param crmId   the fund code
	 * @return the fund summary response
	 */
	@GetMapping(value = "${account.service.account.list.url}")
	String getAccountList(@RequestHeader Map<String, String> headers, @RequestHeader("CRM_ID") String crmId);

	@PostMapping(value = "${account.service.loan.url}", consumes = "application/json", produces = "application/json")
	ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> getLoanAccountDetail(
			@RequestHeader("X-Correlation-ID") String correlationId, @RequestBody AccountId accountId);

	@PostMapping(value = "${account.service.statement.url}", consumes = "application/json", produces = "application/json")
	ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> getLoanAccountStatement(
			@RequestHeader("X-Correlation-ID") String correlationId, @RequestBody LoanStatementRequest request);

	@GetMapping(value = "/product-holdings/{CRM_ID}")
	ResponseEntity<TmbOneServiceResponse<ProductHoldingsResp>> getProductHoldingService(
			@RequestHeader Map<String, String> headers, @PathVariable(value = "CRM_ID") String crmId);

}
