package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.fundallocation.SuggestAllocationDTO;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;


/**
 * ProductExpServiceController request mapping will handle apis call and
 * then navigate to respective method to get MF account Detail
 */
@RequestMapping("/funds")
@RestController
@Api(tags = "Get fund detail and fund rule than return to front-end")
public class ProductExpServiceController {

    private static final TMBLogger<ProductExpServiceController> logger = new TMBLogger<>(ProductExpServiceController.class);

    private final ProductsExpService productsExpService;

    /**
     * Instantiates a new Product exp service controller.
     *
     * @param productsExpService the products exp service
     */
    @Autowired
    public ProductExpServiceController(ProductsExpService productsExpService) {
        this.productsExpService = productsExpService;
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId      the correlation id
     * @param fundAccountRequest the fund account request
     * @return return account full details
     */
    @LogAround
    @ApiOperation(value = "Fetch Fund Detail based on Unit Holder No, Fund House Code And FundCode")
    @PostMapping(value = "/account/detail")
    public ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> getFundAccountDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC,
                    defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody FundAccountRequest fundAccountRequest) throws TMBCommonException {

        HttpHeaders responseHeaders = new HttpHeaders();
        TmbOneServiceResponse<FundAccountResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        FundAccountResponse fundAccountResponse = productsExpService.getFundAccountDetail(correlationId, crmId, fundAccountRequest);

        if (!StringUtils.isEmpty(fundAccountResponse)) {
            oneServiceResponse.setData(fundAccountResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                    ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
            oneServiceResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        }
    }

    /**
     * Gets fund summary.
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return the fund summary
     */
    @ApiOperation(value = "Fetch Fund Summary and Port List based on Unit Holder No and CRMID")
    @LogAround
    @PostMapping(value = "/summary")
    public ResponseEntity<TmbOneServiceResponse<FundSummaryBody>> getFundSummary(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC,
                    defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) throws TMBCommonException {
        TmbOneServiceResponse<FundSummaryBody> oneServiceResponse = new TmbOneServiceResponse<>();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        FundSummaryBody fundSummaryResponse = productsExpService.getFundSummary(correlationId, crmId);
        if (fundSummaryResponse != null) {
            oneServiceResponse.setData(fundSummaryResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                    ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
            oneServiceResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        }
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId            the correlation id
     * @param crmId                    the crm id
     * @param fundPaymentDetailRequest the fund payment detail request
     * @return return list of port, list of account, fund rule and list of holiday
     */
    @ApiOperation(value = "Get all payment detail info than return list of port, list of account, fund rule and list of holiday")
    @LogAround
    @PostMapping(value = "/paymentDetails")
    public ResponseEntity<TmbOneServiceResponse<FundPaymentDetailResponse>> getFundPrePaymentDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC,
                    defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody FundPaymentDetailRequest fundPaymentDetailRequest) throws TMBCommonException {

        TmbOneServiceResponse<FundPaymentDetailResponse> oneServiceResponse = productsExpService.getFundPrePaymentDetail(correlationId, crmId, fundPaymentDetailRequest);
        if (oneServiceResponse.getStatus() != null) {
            if (ProductsExpServiceConstant.SUCCESS_CODE.equals(oneServiceResponse.getStatus().getCode())) {
                return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
            }
        } else {
            return dataNotFoundError(oneServiceResponse);
        }
    }

    /**
     * @param oneServiceResponse the one service response
     * @return
     */
    public ResponseEntity<TmbOneServiceResponse<FundPaymentDetailResponse>> dataNotFoundError(TmbOneServiceResponse<FundPaymentDetailResponse> oneServiceResponse) {
        oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
        oneServiceResponse.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId   the correlation id
     * @param crmId           the crm id
     * @param fundListRequest the fund list request
     * @return return fund list info
     */
    @ApiOperation(value = "Fetch Fund list from MF Service and add more flag, then return to front-end")
    @LogAround
    @PostMapping(value = "/search/fundList")
    public ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> getFundListInfo(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC,
                    defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody FundListRequest fundListRequest) {

        TmbOneServiceResponse<List<FundClassListInfo>> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        List<FundClassListInfo> fundAccountRs = productsExpService.getFundList(correlationId, crmId, fundListRequest);
        if (!StringUtils.isEmpty(fundAccountRs)) {
            oneServiceResponse.setData(fundAccountRs);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                    ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
            oneServiceResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        }
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return return suggest allocation data
     */
    @ApiOperation(value = "Fetch Fund Suggest Allocation")
    @PostMapping(value = "/suggested/allocation")
    public ResponseEntity<TmbOneServiceResponse<SuggestAllocationDTO>> getFundSuggestAllocation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC,
                    defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) throws TMBCommonException {

        TmbOneServiceResponse<SuggestAllocationDTO> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        SuggestAllocationDTO suggestAllocationDto = productsExpService.getSuggestAllocation(correlationId, crmId);
        if (!StringUtils.isEmpty(suggestAllocationDto)) {
            oneServiceResponse.setData(suggestAllocationDto);
            oneServiceResponse.setStatus(getStatusSuccess());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(getStatusNotFund());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        }

    }

    private TmbStatus getStatusNotFund() {
        return new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
    }

    private TmbStatus getStatusSuccess() {
        return new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE);
    }
}