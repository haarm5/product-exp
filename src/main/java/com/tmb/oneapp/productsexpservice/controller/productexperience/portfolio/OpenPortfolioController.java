package com.tmb.oneapp.productsexpservice.controller.productexperience.portfolio;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioValidationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.portfolio.OpenPortfolioService;
import com.tmb.oneapp.productsexpservice.service.productexperience.portfolio.OpenPortfolioValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

/**
 * OpenPortfolioController will handle to call apis for open portfolio validation
 */
@Api(tags = "Get portfolio data then return it back")
@RestController
public class OpenPortfolioController {

    private static final TMBLogger<OpenPortfolioController> logger = new TMBLogger<>(OpenPortfolioController.class);

    private OpenPortfolioService openPortfolioService;

    private OpenPortfolioValidationService openPortfolioValidationService;

    @Autowired
    public OpenPortfolioController(OpenPortfolioService openPortfolioService, OpenPortfolioValidationService openPortfolioValidationService) {
        this.openPortfolioService = openPortfolioService;
        this.openPortfolioValidationService = openPortfolioValidationService;
    }

    /**
     * Description:- method validation to handle cases of open portfolio validation
     *
     * @param correlationId        the correlation id
     * @param crmId                the crm id
     * @param ipAddress            the ip address
     * @param openPortfolioRequest the open portfolio request
     * @return return term and condition with status
     */
    @ApiOperation(value = "Get term and condition with open portfolio status")
    @LogAround
    @PostMapping(value = "/open/portfolio", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ValidateOpenPortfolioResponse>> validateOpenPortfolio(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestHeader(ProductsExpServiceConstant.X_FORWARD_FOR) String ipAddress,
            @Valid @RequestBody OpenPortfolioValidationRequest openPortfolioRequest) {

        TmbOneServiceResponse<ValidateOpenPortfolioResponse> oneServiceResponse =
                openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioRequest);

        if (!StringUtils.isEmpty(oneServiceResponse.getStatus())) {
            if (!oneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return ResponseEntity.badRequest().body(oneServiceResponse);
            }
            return ResponseEntity.ok(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(notFoundStatus());
            return new ResponseEntity(oneServiceResponse, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Description:- Method call to MF service to create customer for open portfolio
     *
     * @param correlationId   the correlation id
     * @param crmId           the crm id
     * @param ipAddress       the ip address
     * @param customerRequest the customer request
     * @return return status of open portfolio
     */
    @ApiOperation(value = "Get term and condition with open portfolio status")
    @LogAround
    @PostMapping(value = "/info/openportfolio", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<OpenPortfolioValidationResponse>> createCustomer(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestHeader(ProductsExpServiceConstant.X_FORWARD_FOR) String ipAddress,
            @Valid @RequestBody CustomerRequest customerRequest) throws TMBCommonException {

        TmbOneServiceResponse<OpenPortfolioValidationResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        OpenPortfolioValidationResponse openPortfolioValidationResponse = openPortfolioService.createCustomer(correlationId, crmId, ipAddress, customerRequest);
        if (!StringUtils.isEmpty(openPortfolioValidationResponse)) {
            return getTmbOneServiceResponseValidationEntity(oneServiceResponse, openPortfolioValidationResponse, ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE, ResponseEntity.ok());
        } else {
            return getTmbOneServiceResponseValidationEntity(oneServiceResponse, null, ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ResponseEntity.status(HttpStatus.NOT_FOUND));
        }
    }

    /**
     * Description:- Method call to MF service to open portfolio
     *
     * @param correlationId            the correlation id
     * @param crmId                    the crm id
     * @param ipAddress                the ip address
     * @param openPortfolioRequestBody the open portfolio request
     * @return return open portfolio data and portfolio nickname
     */
    @ApiOperation(value = "Get open portfolio and portfolio nickname")
    @LogAround
    @PostMapping(value = "/openportfolio", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> openPortfolio(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestHeader(ProductsExpServiceConstant.X_FORWARD_FOR) String ipAddress,
            @Valid @RequestBody OpenPortfolioRequestBody openPortfolioRequestBody) throws TMBCommonException {

        TmbOneServiceResponse<PortfolioResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        PortfolioResponse openPortfolioValidationResponse = openPortfolioService.openPortfolio(correlationId, crmId, ipAddress, openPortfolioRequestBody);
        if (!StringUtils.isEmpty(openPortfolioValidationResponse)) {
            return getTmbOneServiceResponseEntity(oneServiceResponse, openPortfolioValidationResponse, ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE, ResponseEntity.ok());
        } else {
            return getTmbOneServiceResponseEntity(oneServiceResponse, null, ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ResponseEntity.status(HttpStatus.NOT_FOUND));
        }
    }

    @LogAround
    private ResponseEntity<TmbOneServiceResponse<OpenPortfolioValidationResponse>> getTmbOneServiceResponseValidationEntity(TmbOneServiceResponse<OpenPortfolioValidationResponse> oneServiceResponse, OpenPortfolioValidationResponse openPortfolioValidationResponse, String statusCode, String statusMessage, ResponseEntity.BodyBuilder status) {
        oneServiceResponse.setData(openPortfolioValidationResponse);
        oneServiceResponse.setStatus(new TmbStatus(statusCode, statusMessage, ProductsExpServiceConstant.SERVICE_NAME, statusMessage));
        return status.headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }

    @LogAround
    private ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> getTmbOneServiceResponseEntity(TmbOneServiceResponse<PortfolioResponse> oneServiceResponse, PortfolioResponse portfolioResponse, String statusCode, String statusMessage, ResponseEntity.BodyBuilder status) {
        oneServiceResponse.setData(portfolioResponse);
        oneServiceResponse.setStatus(new TmbStatus(statusCode, statusMessage, ProductsExpServiceConstant.SERVICE_NAME, statusMessage));
        return status.headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }
}
