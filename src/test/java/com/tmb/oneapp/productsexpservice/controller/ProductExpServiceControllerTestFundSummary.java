package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class ProductExpServiceControllerTestFundSummary {
    @Mock
    TMBLogger<ProductExpServiceController> logger;
    @Mock
    ProductsExpService productsExpService;
    @InjectMocks
    ProductExpServiceController productExpServiceController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    public void testGetFundSummary() throws Exception {
        FundSummaryResponse expectedResponse = null;

        try {

            ObjectMapper mapper = new ObjectMapper();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/invest_fundsummary.json").toFile(), FundSummaryResponse.class);
            when(productsExpService.getFundSummary(anyString(), any())).thenReturn(expectedResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> result = productExpServiceController.getFundSummary("correlationId", new FundSummaryRq());
        Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
    }
}
