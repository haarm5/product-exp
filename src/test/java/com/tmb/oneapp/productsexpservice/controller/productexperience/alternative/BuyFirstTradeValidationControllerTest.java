package com.tmb.oneapp.productsexpservice.controller.productexperience.alternative;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;

import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buyfirstrade.request.AlternativeBuyFirstTTradeRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response.TradeOccupationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.BuyFirstTradeAlternativeService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyFirstTradeValidationControllerTest {

    @Mock
    public BuyFirstTradeAlternativeService buyFirstTradeAlternativeService;

    @InjectMocks
    public BuyFirstTradeValidationController buyFirstTradeValidationController;

    public static final String correlationId = "correlationID";

    public static final String crmId = "crmId";

    @Test
    public void should_return_success_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() throws TMBCommonException {

        // given
        TmbOneServiceResponse<TradeOccupationResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        when(buyFirstTradeAlternativeService.validationBuyFirstTrade(any(), any(), any())).thenReturn(tmbOneServiceResponse);

        // when
        ResponseEntity<TmbOneServiceResponse<TradeOccupationResponse>> actual = buyFirstTradeValidationController.validationBuyFirstTrade(correlationId, crmId, AlternativeBuyFirstTTradeRequest.builder().build());

        // then
        assertEquals(HttpStatus.OK,actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE,actual.getBody().getStatus().getCode());

    }

    @Test
    public void should_return_bad_request_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() throws TMBCommonException {

        // given
        TmbOneServiceResponse<TradeOccupationResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
        tmbOneServiceResponse.setStatus(tmbStatus);
        when(buyFirstTradeAlternativeService.validationBuyFirstTrade(any(), any(), any())).thenReturn(tmbOneServiceResponse);

        // when
        ResponseEntity<TmbOneServiceResponse<TradeOccupationResponse>> actual = buyFirstTradeValidationController.validationBuyFirstTrade(correlationId, crmId, AlternativeBuyFirstTTradeRequest.builder().build());

        // then
        assertEquals(HttpStatus.BAD_REQUEST,actual.getStatusCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                actual.getBody().getStatus().getCode());

    }

    @Test
    public void should_return_not_found_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() throws TMBCommonException {

        // given
        TmbOneServiceResponse<TradeOccupationResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);
        when(buyFirstTradeAlternativeService.validationBuyFirstTrade(any(), any(), any())).thenReturn(tmbOneServiceResponse);

        // when
        ResponseEntity<TmbOneServiceResponse<TradeOccupationResponse>> actual = buyFirstTradeValidationController.validationBuyFirstTrade(correlationId, crmId, AlternativeBuyFirstTTradeRequest.builder().build());

        // then
        assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                actual.getBody().getStatus().getCode());

    }

}
