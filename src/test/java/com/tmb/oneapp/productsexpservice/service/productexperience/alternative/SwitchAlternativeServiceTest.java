package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.BeanUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SwitchAlternativeServiceTest {

    @Mock
    public AlternativeService alternativeService;

    @Mock
    public CustomerService customerService;

    @InjectMocks
    public SwitchAlternativeService switchAlternativeService;

    public static final String correlationId = "correlationID";

    public static final String crmId = "crmId";

    private void mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums alternativeEnums) {
        // given
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse.builder().build();
        if (alternativeEnums.equals(
                AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY)) {
            customerSearchResponse.setBirthDate("2010-07-08");
        }

        when(customerService.getCustomerInfo(any(), any())).thenReturn(customerSearchResponse);

    }

    private void byPassAllAlternative() {
        TmbStatus successStatus = TmbStatusUtil.successStatus();

        // When
        ValidateServiceHourResponse validateServiceHourResponse = new ValidateServiceHourResponse();

        // Then
        BeanUtils.copyProperties(successStatus, validateServiceHourResponse);
        when(alternativeService.validateServiceHour(any(), any())).thenReturn(validateServiceHourResponse);
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateSuitabilityExpired(any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateIdCardExpired(any(), any())).thenReturn(successStatus);
    }

    @Test
    public void should_return_status_null_when_call_validation_switch_given_correlation_id_and_crm_id_and_alternative_request() {

        // when
        when(customerService.getCustomerInfo(any(), any())).thenThrow(MockitoException.class);
        TmbOneServiceResponse<String> actual = switchAlternativeService.validationSwitch(correlationId, crmId);

        // then
        assertNull(actual.getStatus());
        assertNull(actual.getData());

    }

    @Test
    public void should_return_failed_validate_service_hour_when_call_validation_switch_given_correlation_id_and_crm_id_and_alternative_request() {

        // given
        ValidateServiceHourResponse status = new ValidateServiceHourResponse();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        status.setStartTime("19:00");
        status.setEndTime("20:00");
        when(alternativeService.validateServiceHour(any(), any())).thenReturn(status);

        // when
        TmbOneServiceResponse<String> actual = switchAlternativeService.validationSwitch(correlationId, crmId);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMessage(),
                actual.getStatus().getMessage());
        status.setStartTime("19:00");
        status.setEndTime("20:00");

    }

    @Test
    public void should_return_failed_suidtability_expired_when_call_validation_switch_given_correlation_id_and_crm_id_and_alternative_request() {

        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateSuitabilityExpired(any(), any(), any())).thenReturn(status);

        // when
        TmbOneServiceResponse<String> actual = switchAlternativeService.validationSwitch(correlationId, crmId);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getMessage(),
                actual.getStatus().getMessage());

    }

    @Test
    public void should_return_failed_validate_age_not_over_twenty_when_call_validation_switch_given_correlation_id_and_crm_id_and_alternative_request() {

        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(status);

        // when
        TmbOneServiceResponse<String> actual = switchAlternativeService.validationSwitch(correlationId, crmId);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getMessage(),
                actual.getStatus().getMessage());

    }

    @Test
    public void should_return_failed_customer_risk_level_when_call_validation_switch_given_correlation_id_and_crm_id_and_alternative_request() {

        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), any())).thenReturn(status);

        // when
        TmbOneServiceResponse<String> actual = switchAlternativeService.validationSwitch(correlationId, crmId);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage(),
                actual.getStatus().getMessage());

    }

}
