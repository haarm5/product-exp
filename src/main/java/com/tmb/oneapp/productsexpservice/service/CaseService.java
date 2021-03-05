package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.response.CaseStatusCase;
import com.tmb.oneapp.productsexpservice.model.response.CaseStatusResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static com.tmb.oneapp.productsexpservice.constant.ResponseCode.DATA_NOT_FOUND_ERROR;

@Service
public class CaseService {
    private static final TMBLogger<CaseService> logger = new TMBLogger<>(CaseService.class);
    private final CustomerServiceClient customerServiceClient;

    public CaseService(CustomerServiceClient customerServiceClient) {
        this.customerServiceClient = customerServiceClient;
    }

    /**
     * Get case status and first time use info for customer
     *
     * @param crmId    customer Id
     * @param deviceId device Id
     * @return CaseStatusResponse all case statuses belonging to customer Id
     */
    public CaseStatusResponse getCaseStatus(String correlationId, String crmId, String deviceId, String serviceTypeId) throws TMBCommonException {

        try {
            //GET /apis/customers/firstTimeUsage
            logger.info("Calling GET /apis/customers/firstTimeUsage.");
            CustomerFirstUsage customerFirstUsage = getFirstTimeUsage(crmId, deviceId, serviceTypeId);
            logger.info("GET /apis/customers/firstTimeUsage response: {}", customerFirstUsage);

            //POST /apis/customers/firstTimeUsage
            if (customerFirstUsage == null) {
                logger.info("Calling POST /apis/customers/firstTimeUsage.");
                asyncPostFirstTime(crmId, deviceId, serviceTypeId);
            }

            //GET /apis/customer/case/status/{CRM_ID}.
            logger.info("Calling GET /apis/customer/case/status/{CRM_ID}");
            List<CaseStatusCase> caseStatusList = getCaseStatus(correlationId, crmId);
            logger.info("GET /apis/customer/case/status/{CRM_ID} response: {}", caseStatusList);

            //Separate According to Status
            List<CaseStatusCase> inProgress = new ArrayList<>();
            List<CaseStatusCase> completed = new ArrayList<>();

            caseStatusList.forEach(caseStatusCase -> {
                if (caseStatusCase.getStatus().equals(CASE_STATUS_IN_PROGRESS)) {
                    inProgress.add(caseStatusCase);
                } else if (caseStatusCase.getStatus().equals(CASE_STATUS_CLOSED)) {
                    completed.add(caseStatusCase);
                }
            });

            return new CaseStatusResponse()
                    .setServiceTypeId(serviceTypeId)
                    .setFirstUsageExperience(customerFirstUsage == null)
                    .setInProgress(inProgress)
                    .setCompleted(completed);

        } catch (Exception e) {
            logger.error("Error calling GET /case/status : {}", e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }

    }

    /**
     * Check customer first time use
     *
     * @param crmId    customer Id
     * @param deviceId device Id
     * @return CustomerFirstUsage information of first time use
     */
    @SuppressWarnings("all")
    public CustomerFirstUsage getFirstTimeUsage(String crmId, String deviceId, String serviceTypeId) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> getFirstTimeUsageResponse =
                    customerServiceClient.getFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (getFirstTimeUsageResponse.getBody() != null &&
                    getFirstTimeUsageResponse.getBody().getStatus() != null &&
                    SUCCESS_CODE.equals(getFirstTimeUsageResponse.getBody().getStatus().getCode())) {
                return getFirstTimeUsageResponse.getBody().getData();
            }
            return null;
        } catch (FeignException e) {
            TmbOneServiceResponse response = mapTmbOneServiceResponse(e.responseBody());

            if (response != null && response.getStatus() != null && DATA_NOT_FOUND_ERROR.getCode().equals(response.getStatus().getCode())) {
                logger.info("Data not found in database. crmId: {}, deviceId {}", crmId, deviceId);
                return null;
            } else {
                logger.error("Unexpected error occured : {}", e);
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            logger.error("Error getting first time usage data. crmId: {}, deviceId {}, error: {}", crmId, deviceId, e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }
    }

    /**
     * Insert customer first time use
     *
     * @param crmId    customer Id
     * @param deviceId device Id
     */
    @Async
    public void asyncPostFirstTime(String crmId, String deviceId, String serviceTypeId) {
        try {
            ResponseEntity<TmbOneServiceResponse<String>> response =
                    customerServiceClient.postFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                logger.info("Async call to insert first time usage completed successfully. " +
                        "crmId: {}, deviceId: {}", crmId, deviceId);
            }
        } catch (Exception e) {
            logger.error("Async call to insert first time usage failed. " +
                    "crmId: {}, deviceId: {}, error: {}", crmId, deviceId, e);
        }

    }

    /**
     * Get customer case information
     *
     * @param crmId customer Id
     * @return list of all case status
     */
    @SuppressWarnings("all")
    public List<CaseStatusCase> getCaseStatus(String correlationId, String crmId) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<List<CaseStatusCase>>> getCaseStatusResponse =
                    customerServiceClient.getCaseStatus(correlationId, crmId);

            if (getCaseStatusResponse.getBody() != null &&
                    getCaseStatusResponse.getBody().getStatus() != null &&
                    SUCCESS_CODE.equals(getCaseStatusResponse.getBody().getStatus().getCode())) {
                return getCaseStatusResponse.getBody().getData();
            }
            return new ArrayList<>();
        } catch (FeignException e) {
            TmbOneServiceResponse response = mapTmbOneServiceResponse(e.responseBody());

            if (response != null && response.getStatus() != null && DATA_NOT_FOUND_ERROR.getCode().equals(response.getStatus().getCode())) {
                logger.info("Data not found. crmId: {}", crmId);
                return new ArrayList<>();
            } else {
                logger.error("Unexpected error occured : {}", e);
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            logger.error("Unexpected error occured : {}", e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @SuppressWarnings("all")
    public TmbOneServiceResponse mapTmbOneServiceResponse(Optional<ByteBuffer> optionalResponse) {
        try {
            if(!optionalResponse.isPresent()){
                return null;
            }

            String respBody = StandardCharsets.UTF_8.decode(optionalResponse.get()).toString();
            return (TmbOneServiceResponse) TMBUtils.convertStringToJavaObj(respBody, TmbOneServiceResponse.class);
        } catch (Exception e) {
            logger.error("Unexpected error received, cannot parse.");
            return null;
        }
    }

}
