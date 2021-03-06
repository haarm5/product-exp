package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.information.InformationDto;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.request.TradeOccupationRequest;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InformationServiceTest {

    @Mock
    private TMBLogger<InformationServiceTest> logger;

    @Mock
    private InvestmentAsyncService investmentAsyncService;

    @InjectMocks
    private InformationService informationService;

    @Test
    void should_return_information_dto_when_call_get_fund_information_given_correlation_id_and_fund_code_request_body() throws IOException, TMBCommonException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();

        InformationResponse informationResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_information.json").toFile(),
                InformationResponse.class);
        when(investmentAsyncService.fetchFundInformation(any(), any())).thenReturn(CompletableFuture.completedFuture(informationResponse.getData()));

        DailyNavResponse dailyNavResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_daily_nav.json").toFile(),
                DailyNavResponse.class);
        when(investmentAsyncService.fetchFundDailyNav(any(), any())).thenReturn(CompletableFuture.completedFuture(dailyNavResponse.getData()));

        //When
        InformationDto actual = informationService.getFundInformation(correlationId, fundCodeRequestBody);

        //Then
        InformationDto expected = InformationDto.builder()
                .information(informationResponse.getData())
                .dailyNav(dailyNavResponse.getData())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void should_return_null_when_call_get_fund_information_given_throw_runtime_exception_from_product_exp_async_service() throws TMBCommonException {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();

        when(investmentAsyncService.fetchFundInformation(any(), any())).thenThrow(TMBCommonException.class);

        //When
        InformationDto actual = informationService.getFundInformation(correlationId, fundCodeRequestBody);

        //Then
        assertNull(actual);
    }

    @Test
    void should_throw_common_exception_with_error_and_message_when_call_trade_ouccupation_inquiry_give_correlation_id_and_crm_id_and_trade_occupation_request() throws Exception {

        // Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();

        String errorCode = "2000005";
        String errorMessage = "Bad Request";
        when(investmentAsyncService.fetchFundInformation(any(), any())).thenThrow(getMockCommonException(errorCode,errorMessage));


        // when
        try {
            informationService.getFundInformation(correlationId, fundCodeRequestBody);
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