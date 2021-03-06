package com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice;


import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.BuyFlowFirstTrade;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
import org.springframework.beans.BeanUtils;

public class ValidateGroupingAbstractService {

    protected final AlternativeService alternativeService;

    public ValidateGroupingAbstractService(AlternativeService alternativeService) {
        this.alternativeService = alternativeService;
    }

    protected TmbOneServiceResponse<String> validateSuitabilityExpired(String correlationId,
                                                                       String crmId,
                                                                       TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                                       TmbStatus status) {
        // validate suitability expired
        tmbOneServiceResponse.setStatus(alternativeService.validateSuitabilityExpired(correlationId, crmId, status));
        return tmbOneServiceResponse;
    }

    protected TmbOneServiceResponse<String> validateGroupingService(String correlationId,
                                                                    CustomerSearchResponse customerInfo,
                                                                    TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                                    TmbStatus status,
                                                                    BuyFlowFirstTrade buyFlowFirstTrade) {

        // validate service hour
        ValidateServiceHourResponse validateServiceHourResponse = alternativeService.validateServiceHour(correlationId, status);
        BeanUtils.copyProperties(validateServiceHourResponse, tmbOneServiceResponse.getStatus());
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
            tmbOneServiceResponse.setData(String.format("%s-%s", validateServiceHourResponse.getStartTime(), validateServiceHourResponse.getEndTime()));
            return tmbOneServiceResponse;
        }

        // validate age should > 20
        tmbOneServiceResponse.setStatus(alternativeService.validateDateNotOverTwentyYearOld(customerInfo.getBirthDate(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
            return tmbOneServiceResponse;
        }

        // validate customer risk level
        tmbOneServiceResponse.setStatus(alternativeService.validateCustomerRiskLevel(correlationId, customerInfo, status, buyFlowFirstTrade));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SERVICE_NOT_READY)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
            }
            return tmbOneServiceResponse;
        }

        return tmbOneServiceResponse;
    }
}
