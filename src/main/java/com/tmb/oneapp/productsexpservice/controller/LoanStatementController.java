package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.model.loan.LoanStatementRequest;
import com.tmb.oneapp.productsexpservice.model.loan.LoanStatementResponse;
import com.tmb.oneapp.productsexpservice.model.loan.Statement;
import io.swagger.annotations.Api;
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

import java.time.Instant;
import java.util.List;

@RestController
@Api(tags = "Fetch Home loan account statement")
public class LoanStatementController {
    private static final TMBLogger<LoanStatementController> log = new TMBLogger<>(LoanStatementController.class);
    private final AccountRequestClient accountRequestClient;


    /**
     * Constructor
     *
     * @param accountRequestClient
     */
    @Autowired
    public LoanStatementController(AccountRequestClient accountRequestClient) {
        this.accountRequestClient = accountRequestClient;


    }


    /**
     * @param correlationId
     * @param requestBody
     * @return
     */
    @LogAround
    @PostMapping(value = "/loan/get-loan-statement", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> getLoanStatement(@ApiParam(value = "X_CORRELATION_ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true) @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
                                                                                         @ApiParam(value = "Account ID , start date, end date", defaultValue = "00016109738001", required = true) @RequestBody LoanStatementRequest requestBody) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<LoanStatementResponse> serviceResponse = new TmbOneServiceResponse<>();


        try {

            String accountId = requestBody.getAccountId();
            String startDate = requestBody.getStartDate();
            String endDate = requestBody.getEndDate();
            if (!Strings.isNullOrEmpty(accountId) && !Strings.isNullOrEmpty(startDate) && !Strings.isNullOrEmpty(endDate)) {
                ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> loanResponse = accountRequestClient.getLoanAccountStatement(correlationId, requestBody);
                int statusCodeValue = loanResponse.getStatusCodeValue();
                HttpStatus statusCode = loanResponse.getStatusCode();

                if (loanResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

                    LoanStatementResponse loanDetails = loanResponse.getBody().getData();
                    List<Statement> statements = loanDetails.getResponse().getStatements();
                    return getTmbOneServiceResponse(responseHeaders, serviceResponse, loanDetails, statements);
                } else {
                    return getTmbOneServiceResponseResponseEntity(responseHeaders, serviceResponse);

                }
            } else {
                return getTmbOneServiceResponseResponseEntity(responseHeaders, serviceResponse);
            }

        } catch (Exception e) {
            return failedErrorResponse(responseHeaders, serviceResponse, e);
        }

    }

    ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> getTmbOneServiceResponse(HttpHeaders responseHeaders, TmbOneServiceResponse<LoanStatementResponse> serviceResponse, LoanStatementResponse loanDetails, List<Statement> statements) {
        statements.sort((Statement s1, Statement s2) -> s2.getTransactionDate().compareTo(s1.getTransactionDate()));
        loanDetails.getResponse().setStatements(statements);
        serviceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

        serviceResponse.setData(loanDetails);
        return ResponseEntity.ok().headers(responseHeaders).body(serviceResponse);
    }

    ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> failedErrorResponse(HttpHeaders responseHeaders, TmbOneServiceResponse<LoanStatementResponse> serviceResponse, Exception e) {
        log.error("Error while getting LoanAccountStatement: {}", e);
        serviceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService()));
        return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
    }

    /**
     * @param responseHeaders
     * @param serviceResponse
     * @return
     */
    ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> getTmbOneServiceResponseResponseEntity(HttpHeaders responseHeaders, TmbOneServiceResponse<LoanStatementResponse> serviceResponse) {
        serviceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
        return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
    }
}

