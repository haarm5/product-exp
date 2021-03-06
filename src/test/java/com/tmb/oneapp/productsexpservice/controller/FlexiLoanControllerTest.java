package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CheckSystemOffResponse;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.FlexiLoanConfirmRequest;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.FlexiLoanConfirmResponse;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import com.tmb.oneapp.productsexpservice.service.FlexiCheckSystemOffService;
import com.tmb.oneapp.productsexpservice.service.FlexiLoanConfirmService;
import com.tmb.oneapp.productsexpservice.service.FlexiLoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FlexiLoanControllerTest {

    @Mock
    FlexiLoanConfirmService flexiLoanConfirmService;
    @Mock
    FlexiLoanService flexiLoanService;
    @Mock
    FlexiCheckSystemOffService flexiCheckSystemOffService;
    @InjectMocks
    FlexiLoanController flexiLoanController;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        flexiLoanController = new FlexiLoanController(flexiLoanConfirmService, flexiLoanService,flexiCheckSystemOffService);
    }

    @Test
    public void testGetSubmissionInfoSuccess() throws TMBCommonException {
        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaId(1L);
        String correlationId = "xxx";
        SubmissionInfoResponse response = new SubmissionInfoResponse();
        when(flexiLoanService.getSubmissionInfo(any(),any())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<SubmissionInfoResponse>> responseEntity = flexiLoanController.getSubmissionInfo(correlationId, request);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetSubmissionInfoFail() throws TMBCommonException {
        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaId(1L);
        String correlationId = "xxx";
        when(flexiLoanService.getSubmissionInfo(any(),any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<SubmissionInfoResponse>> responseEntity = flexiLoanController.getSubmissionInfo(correlationId, request);
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testConfirmFlexiLoanSuccess() throws Exception {
        FlexiLoanConfirmRequest request = new FlexiLoanConfirmRequest();
        request.setCaID(1L);
        request.setProductCode("code");
        request.setProductNameTH("name");
        FlexiLoanConfirmResponse response = new FlexiLoanConfirmResponse();
        when(flexiLoanConfirmService.confirm(any(), any())).thenReturn(response);
        Map<String, String> requestHeader = new HashMap<>();
        ResponseEntity<TmbOneServiceResponse<FlexiLoanConfirmResponse>> responseEntity = flexiLoanController.submit(requestHeader, request);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testConfirmFlexiLoanFail() throws Exception {
        FlexiLoanConfirmRequest request = new FlexiLoanConfirmRequest();
        request.setCaID(1L);
        request.setProductCode("code");
        request.setProductNameTH("name");
        when(flexiLoanConfirmService.confirm(any(), any())).thenThrow(new IllegalArgumentException());
        Map<String, String> requestHeader = new HashMap<>();
        ResponseEntity<TmbOneServiceResponse<FlexiLoanConfirmResponse>> responseEntity = flexiLoanController.submit(requestHeader, request);
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testCheckSystemOffSuccess()  {
        String correlationId = "xxx";
        CheckSystemOffResponse response = new CheckSystemOffResponse();
        when(flexiCheckSystemOffService.checkSystemOff(any())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<CheckSystemOffResponse>> responseEntity = flexiLoanController.checkSystemOff(correlationId);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testCheckSystemOffFail()  {
        String correlationId = "xxx";
        when(flexiCheckSystemOffService.checkSystemOff(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<CheckSystemOffResponse>> responseEntity = flexiLoanController.checkSystemOff(correlationId);
        assertTrue(responseEntity.getStatusCode().isError());
    }

}
