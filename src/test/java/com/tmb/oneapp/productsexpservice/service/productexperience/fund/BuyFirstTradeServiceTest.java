package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.request.TradeOccupationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response.TradeOccupationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyFirstTradeServiceTest {

    @Mock
    public InvestmentAsyncService investmentAsyncService;

    @InjectMocks
    public BuyFirstTradeService buyFirstTradeService;

    private final String crmId = "001100000000000000000012035644";

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    @Test
    void should_return_status_0000_and_body_not_null_when_call_trade_ouccupation_inquiry_give_correlation_id_and_crm_id_and_trade_occupation_request() throws Exception {

        // Given
        ObjectMapper mapper = new ObjectMapper();
        FirstTradeResponseBody firstTradeResponseBody = mapper.readValue(Paths.get("src/test/resources/investment/fund/first_trade_body.json").toFile(),
                FirstTradeResponseBody.class);
        when(investmentAsyncService.getFirstTrade(any(), any())).thenReturn(CompletableFuture.completedFuture(firstTradeResponseBody));

        OccupationInquiryResponseBody occupationInquiryResponseBody = mapper.readValue(Paths.get("src/test/resources/investment/customer/occupation_inquiry_body.json").toFile(),
                OccupationInquiryResponseBody.class);
        when(investmentAsyncService.fetchOccupationInquiry(any(), any())).thenReturn(CompletableFuture.completedFuture(occupationInquiryResponseBody));

        // when
        TmbOneServiceResponse<TradeOccupationResponse> actual = buyFirstTradeService
                .tradeOuccupationInquiry(correlationId,crmId,TradeOccupationRequest.builder().build());

        // then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE,actual.getStatus().getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE,actual.getStatus().getMessage());

    }

    @Test
    void should_return_null_when_call_trade_ouccupation_inquiry_give_correlation_id_and_crm_id_and_trade_occupation_request() throws Exception {

        // Given
        when(investmentAsyncService.getFirstTrade(any(), any())).thenThrow(MockitoException.class);

        // when
        TmbOneServiceResponse<TradeOccupationResponse> actual = buyFirstTradeService
                .tradeOuccupationInquiry(correlationId,crmId,TradeOccupationRequest.builder().build());

        // then
        assertNull(actual.getStatus());
        assertNull(actual.getData());

    }

    @Test
    void should_throw_common_exception_with_error_and_message_when_call_trade_ouccupation_inquiry_give_correlation_id_and_crm_id_and_trade_occupation_request() throws Exception {

        // Given
        String errorCode = "2000005";
        String errorMessage = "Bad Request";
        when(investmentAsyncService.getFirstTrade(any(), any())).thenThrow(getMockCommonException(errorCode,errorMessage));

        // when
        try {
            buyFirstTradeService
                    .tradeOuccupationInquiry(correlationId,crmId,TradeOccupationRequest.builder().build());
        }catch (TMBCommonException e){

            // then
            assertEquals(errorCode,e.getErrorCode());
            assertEquals(errorMessage,e.getErrorMessage());

        }
    }

    private TMBCommonException getMockCommonException(String errorCode, String errorMessage){
        return new TMBCommonException(
                errorCode,
                errorMessage,
                ProductsExpServiceConstant.SERVICE_NAME,
                HttpStatus.BAD_REQUEST,null);
    }

}
