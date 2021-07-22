package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductRequest;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import com.tmb.oneapp.productsexpservice.model.response.CodeEntry;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkProfileInfoResponse;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.LendingRslStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * LendingServiceClient to retrieve lending data
 */
@FeignClient(name = "${lending.service.name}", url = "${lending.service.url}")
public interface LendingServiceClient {

	/**
	 * Call RSL System to get application status
	 *
	 * @return RSL application statuses
	 */
	@GetMapping(value = "/apis/lending-service/rsl/status")
	ResponseEntity<TmbOneServiceResponse<List<LendingRslStatusResponse>>> getLendingRslStatus(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_CITIZEN_ID) String citizenId,
            @RequestHeader(HEADER_MOBILE_NO) String mobileNo);

	/**
	 * Call RSL Criteria for WorkStatusInfo
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/status")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getWorkStatusInfo(
			@RequestHeader(HEADER_X_CORRELATION_ID) String correlationId);

	/**
	 * Call RSL Criteria for OccupationByOccupationCode
	 * 
	 * @param correlationId
	 * @param reference
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/status/{entrycode}")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getWorkStatusInfo(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @PathVariable("entrycode") String reference);

	/**
	 * Call RSL Criteria for get business type information
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/businesstype")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getBusinessTypeInfo(
			@RequestHeader(HEADER_X_CORRELATION_ID) String correlationId);

	/**
	 * Call RSL Criteria for get business type information
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/businesstype/{entrycode}")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getBusinessSubTypeInfo(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @PathVariable("entrycode") String reference);

	/**
	 * Call RSL Criteria for get source of income
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/income/{entryCode}")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getSourceOfIncomeInfo(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @PathVariable("entryCode") String reference);

	/**
	 * Call RSL Criteria for country information
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/country")

	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getCountryList(
			@RequestHeader(HEADER_X_CORRELATION_ID) String correlationId);

	/**
	 * Call RSL Criteria for working information
	 * 
	 * @param correlationId
	 * @param occupationCode
	 * @param businessTypeCode
	 * @param countryOfIncome
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/fetch-working-info")
	ResponseEntity<TmbOneServiceResponse<WorkProfileInfoResponse>> getWorkInformationWithProfile(
			@RequestHeader(HEADER_X_CORRELATION_ID) String correlationId,
			@RequestParam(value = "occupationcode") String occupationCode,
			@RequestParam(value = "businesstypecode") String businessTypeCode,
			@RequestParam(value = "countryofincome") String countryOfIncome);

	/**
	 * Get Flexi Loan products
	 *
	 * @param correlationId
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/apis/lending-service/loan/products")
	ResponseEntity<TmbOneServiceResponse<Object>> getLoanProducts(@RequestHeader(HEADER_X_CORRELATION_ID) String correlationId,
			@RequestBody ProductRequest request);

	@GetMapping(value = "/apis/lending-service/flexiLoan/approvedStatus")
	ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> checkApprovedStatus(@Valid InstantLoanCalUWRequest request);

	@GetMapping(value = "/apis/lending-service/flexiLoan/submissionInfo")
	ResponseEntity<TmbOneServiceResponse<SubmissionInfoResponse>> submissionInfo(
			@RequestHeader(HEADER_X_CORRELATION_ID) String correlationId,
			@Valid SubmissionInfoRequest request);

}
