package com.tmb.oneapp.productsexpservice.controller.productexperience.alternative;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.BuyAlternativeService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyValidationControllerTest {

    @Mock
    public BuyAlternativeService buyAlternativeService;

    @InjectMocks
    public BuyValidationController buyValidationController;

    public static final String correlationId = "correlationID";

    public static final String crmId = "crmId";

    public static final String ipAddress = "0.0.0.0";

    @Test
    void should_return_success_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        when(buyAlternativeService.validationBuy(anyString(), anyString(), anyString(), any())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<String>> actual = buyValidationController.validationBuy(correlationId, crmId, ipAddress, AlternativeBuyRequest.builder().build());

        // Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getBody().getStatus().getCode());
    }

    @Test
    void should_return_bad_request_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
        tmbOneServiceResponse.setStatus(tmbStatus);
        when(buyAlternativeService.validationBuy(anyString(), anyString(), anyString(), any())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<String>> actual = buyValidationController.validationBuy(correlationId, crmId, ipAddress, AlternativeBuyRequest.builder().build());

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                actual.getBody().getStatus().getCode());
    }

    @Test
    void should_return_bad_request_status_with_service_hour_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());

        ValidateServiceHourResponse validateServiceHourResponse = new ValidateServiceHourResponse();
        validateServiceHourResponse.setCode(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
        validateServiceHourResponse.setMessage(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMessage());
        validateServiceHourResponse.setDescription(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getDescription());
        BeanUtils.copyProperties(validateServiceHourResponse, tmbOneServiceResponse.getStatus());
        tmbOneServiceResponse.setData("19:00-20:00");
        when(buyAlternativeService.validationBuy(anyString(), anyString(), anyString(), any())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<String>> actual = buyValidationController.validationBuy(correlationId, crmId, ipAddress, AlternativeBuyRequest.builder().build());

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode(),
                actual.getBody().getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMessage(),
                actual.getBody().getStatus().getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getDescription(),
                actual.getBody().getStatus().getDescription());
        assertEquals("19:00-20:00", actual.getBody().getData());
    }

    @Test
    void should_return_not_found_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);
        when(buyAlternativeService.validationBuy(anyString(), anyString(), anyString(), any())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<String>> actual = buyValidationController.validationBuy(correlationId, crmId, ipAddress, AlternativeBuyRequest.builder().build());

        // Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                actual.getBody().getStatus().getCode());
    }
}
