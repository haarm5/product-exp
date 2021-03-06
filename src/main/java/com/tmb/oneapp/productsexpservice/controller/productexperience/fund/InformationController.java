package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.information.InformationDto;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.InformationService;
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

/**
 * InformationController request will handle to call apis for combining the data from info and daily nav of investment
 */
@Api(tags = "Get fund detail and fund rule than return to front-end")
@RequestMapping("/funds")
@RestController
public class InformationController {

    private static final TMBLogger<InformationController> logger = new TMBLogger<>(InformationController.class);

    private InformationService informationService;

    @Autowired
    public InformationController(InformationService informationService) {
        this.informationService = informationService;
    }

    /**
     * Description:- Inquiry fund information from MF Service
     *
     * @param correlationId       the correlation id
     * @param fundCodeRequestBody the information request body
     * @return return fund list info
     */
    @ApiOperation(value = "Fetch fund information from MF Service, then return to front-end")
    @LogAround
    @PostMapping(value = "/info")
    public ResponseEntity<TmbOneServiceResponse<InformationDto>> getFundInformation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestBody FundCodeRequestBody fundCodeRequestBody) throws TMBCommonException {

        TmbOneServiceResponse<InformationDto> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        InformationDto fundInformation = informationService.getFundInformation(correlationId, fundCodeRequestBody);
        if (!StringUtils.isEmpty(fundInformation)) {
            return getTmbOneServiceResponseEntity(oneServiceResponse, fundInformation, ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE, ResponseEntity.ok());
        } else {
            return getTmbOneServiceResponseEntity(oneServiceResponse, null, ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ResponseEntity.status(HttpStatus.NOT_FOUND));
        }
    }

    private ResponseEntity<TmbOneServiceResponse<InformationDto>> getTmbOneServiceResponseEntity(TmbOneServiceResponse<InformationDto> oneServiceResponse, InformationDto informationDto, String statusCode, String statusMessage, ResponseEntity.BodyBuilder status) {
        oneServiceResponse.setData(informationDto);
        oneServiceResponse.setStatus(new TmbStatus(statusCode, statusMessage, ProductsExpServiceConstant.SERVICE_NAME, statusMessage));
        return status.headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }
}
