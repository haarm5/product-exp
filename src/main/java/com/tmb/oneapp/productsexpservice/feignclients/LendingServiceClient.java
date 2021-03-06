package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.model.loan.InstantLoanCreationRequest;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.lending.document.*;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailResponse;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductRequest;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.ChecklistResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailSaveInfoRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateWorkingDetailReq;
import com.tmb.oneapp.productsexpservice.model.response.CodeEntry;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.*;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionApplicationDetail;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.LendingRslStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
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
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader(HEADER_CITIZEN_ID) String citizenId, @RequestHeader(HEADER_MOBILE_NO) String mobileNo);

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
    ResponseEntity<TmbOneServiceResponse<Object>> getLoanProducts(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestBody ProductRequest request);

    @GetMapping(value = "/apis/lending-service/flexiLoan/approvedStatus")
    ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> checkApprovedStatus(
            @RequestParam(value = "caId") BigDecimal caId,
            @RequestParam(value = "triggerFlag") String triggerFlag,
            @RequestParam(value = "product") String product,
            @RequestParam(value = "loanDay1Set") String loanDay1Set);

    @GetMapping(value = "/apis/lending-service/flexiLoan/submissionInfo")
    ResponseEntity<TmbOneServiceResponse<SubmissionInfoResponse>> submissionInfo(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestParam(value = "caId") Long caId,
            @RequestParam(value = "productCode") String productCode);

    @GetMapping(value = "/apis/lending-service/loanOnlineSubmission/personalDetail")
    ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> getPersonalDetail(
            @RequestHeader(HEADER_X_CRM_ID) String crmid, @RequestParam(value = "caId") Long caId);

    @PostMapping(value = "/apis/lending-service/loanOnlineSubmission/savePersonalDetail")
    ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> saveCustomerInfo(
            @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody PersonalDetailSaveInfoRequest personalDetailReg);

    @GetMapping(value = "/apis/lending-service/loanOnlineSubmission/getIncomeInfo")
    ResponseEntity<TmbOneServiceResponse<IncomeInfo>> getIncomeInfo(@RequestHeader(HEADER_X_CRM_ID) String crmId);

    @PostMapping(value = "/apis/lending-service/loanOnlineSubmission")
    ResponseEntity<TmbOneServiceResponse<ResponseApplication>> createApplication(
            @RequestHeader(HEADER_X_CRM_ID) String crmId, @RequestBody LoanSubmissionCreateApplicationReq request);

    @GetMapping(value = "/apis/lending-service/dropdown/loanSubmission/workingDetail")
    ResponseEntity<TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail>> getDropdownLoanSubmissionWorkingDetail(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestParam(value = "caId") String caId);

    @GetMapping(value = "/apis/lending-service/dropdown/loanSubmission/applicationDetail")
    ResponseEntity<TmbOneServiceResponse<DropdownsLoanSubmissionApplicationDetail>> getDropdownLoanSubmissionApplicationDetail(
            @RequestParam(value = "productType") String productType);

    @PutMapping(value = "/apis/lending-service/loanOnlineSubmission/updateWorkingDetail")
    ResponseEntity<TmbOneServiceResponse<ResponseApplication>> updateWorkingDetail(
            @RequestBody UpdateWorkingDetailReq request);

    @GetMapping(value = "/apis/lending-service/loanOnlineSubmission/documents")
    ResponseEntity<TmbOneServiceResponse<List<ChecklistResponse>>> getDocuments(
            @RequestHeader(HEADER_X_CRM_ID) String crmId, @RequestParam(value = "caId") Long caId);

    @GetMapping(value = "/apis/lending-service/loanOnlineSubmission/documents/more")
    ResponseEntity<TmbOneServiceResponse<List<ChecklistResponse>>> getMoreDocuments(
            @RequestHeader(HEADER_X_CRM_ID) String crmId, @RequestParam(value = "caId") Long caId);

    @GetMapping(value = "/apis/lending-service/loanOnlineSubmission/getWorkingDetail")
    ResponseEntity<TmbOneServiceResponse<WorkingDetail>> getLoanSubmissionWorkingDetail(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestParam(value = "caId") Long caId);

    @PostMapping(value = "/apis/lending-service/loanOnlineSubmission/get-customer-information")
    ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> getCustomerInformation(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody UpdateNCBConsentFlagRequest request);

    @PostMapping(value = "/apis/lending-service/document/upload")
    ResponseEntity<TmbOneServiceResponse<UploadDocumentResponse>> uploadDocument(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody UploadDocumentRequest request);

    @PostMapping(value = "/apis/lending-service/document/submit")
    ResponseEntity<TmbOneServiceResponse<SubmitDocumentResponse>> submitDocument(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody SubmitDocumentRequest request);

    @PostMapping(value = "/apis/lending-service/document/submit/more")
    ResponseEntity<TmbOneServiceResponse<SubmitDocumentResponse>> submitMoreDocument(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody SubmitDocumentRequest request);

    @DeleteMapping(value = "/apis/lending-service/document/{caId}/{docCode}/{fileType}/{fileName}")
    ResponseEntity<TmbOneServiceResponse<DeleteDocumentResponse>> deleteDocument(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @PathVariable("caId") String caId, @PathVariable("docCode") String docCode, @PathVariable("fileType") String fileType, @PathVariable("fileName") String fileName);

    @PostMapping(value = "/apis/lending-service/loanOnlineSubmission/update-flag-and-store-ncb-consent")
    ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> updateNCBConsentFlagAndStoreFile(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody UpdateNCBConsentFlagRequest request);

    @PostMapping(value = "/apis/lending-service/create-instant-loan-application")
    ResponseEntity<TmbOneServiceResponse<Object>> createInstanceLoanApplication(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody InstantLoanCreationRequest request);

    @GetMapping(value = "/apis/lending-service/loanOnlineSubmission/verifyCustomer")
    ResponseEntity<TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse>> getCustomerAge(
            @RequestHeader(HEADER_X_CRM_ID) String crmId);

    @PostMapping(value = "/apis/lending-service/loan/product-orientation")
    ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> fetchProductOrientation(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody ProductDetailRequest request);

    @GetMapping(value = "/apis/lending-service/loanOnlineSubmission/e-app")
    ResponseEntity<TmbOneServiceResponse<EAppResponse>> getEApp(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestParam(value = "caId") Long caId);

    @PostMapping(value = "/apis/lending-service/rsl/LoanSubmissionInstantLoanTransferApplication")
    ResponseEntity<TmbOneServiceResponse<com.tmb.common.model.legacy.rsl.ws.instant.transfer.response.ResponseTransfer>> transferApplication(
            @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody com.tmb.common.model.legacy.rsl.ws.instant.transfer.request.Body request);


    @GetMapping(value = "/apis/lending-service/loan/preloadLoanCalculator")
    ResponseEntity<TmbOneServiceResponse<LoanCalculatorResponse>> getPreloadLoanCalculator(
            @RequestParam(value = "caId") Long caId,
            @RequestParam(value = "product") String product);

    @PutMapping(value = "/apis/lending-service/loanOnlineSubmission/updateApplication")
    ResponseEntity<TmbOneServiceResponse<ResponseApplication>> updateApplication(
            @RequestHeader(HEADER_X_CRM_ID) String crmId, @RequestBody LoanSubmissionCreateApplicationReq request);

}
