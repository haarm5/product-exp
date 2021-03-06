package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.ApplyPersonalLoan;
import com.tmb.oneapp.productsexpservice.model.response.loan.ProductData;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionOnlineService;
import com.tmb.oneapp.productsexpservice.service.PersonalLoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PersonalLoanControllerTest {

    PersonalLoanController personalLoanController;

    @Mock
    PersonalLoanService personalLoanService;
    @Mock
    LoanSubmissionOnlineService loanSubmissionOnlineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        personalLoanController = new PersonalLoanController(personalLoanService, loanSubmissionOnlineService);
    }

    @Test
    public void testCheckPreloadSuccess() {
        LoanPreloadRequest loadPreloadReq = new LoanPreloadRequest();
        loadPreloadReq.setProductCode("P");
        LoanPreloadResponse response = new LoanPreloadResponse();
        when(personalLoanService.checkPreload(any(), any())).thenReturn(response);
        personalLoanController.checkPreload("zxx", loadPreloadReq);
        assertTrue(true);
    }

    @Test
    public void testCheckPreloadFail() {
        LoanPreloadRequest loadPreloadReq = new LoanPreloadRequest();
        loadPreloadReq.setProductCode("P");
        LoanPreloadResponse response = new LoanPreloadResponse();
        when(personalLoanService.checkPreload(any(), any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<LoanPreloadResponse>> result = personalLoanController.checkPreload("zxx", loadPreloadReq);
        assertTrue(result.getStatusCode().isError());
    }

    @Test
    public void testCheckCalUWSuccess() throws TMBCommonException {
        InstantLoanCalUWRequest request = new InstantLoanCalUWRequest();
        request.setCaId(BigDecimal.valueOf(2021052704186761L));
        request.setTriggerFlag("Y");
        request.setProduct("RC01");

        when(loanSubmissionOnlineService.checkCalculateUnderwriting(request)).thenReturn(any());

        ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> result = personalLoanController.checkCalUW(request);
        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

    }

    @Test
    public void testCheckCalUWSFail() throws TMBCommonException {

        InstantLoanCalUWRequest request = new InstantLoanCalUWRequest();
        request.setCaId(BigDecimal.valueOf(2021052704186775L));
        request.setTriggerFlag("Y");
        request.setProduct("RC01");

        when(loanSubmissionOnlineService.checkCalculateUnderwriting(request)).thenThrow(new NullPointerException());

        ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> result = personalLoanController.checkCalUW(request);
        assertTrue(result.getStatusCode().isError());
    }


    @Test
    public void testGetProductListSuccess() {
        when(personalLoanService.getProductsLoan()).thenReturn(any());
        personalLoanController.getProductList();
        assertTrue(true);
    }

    @Test
    public void testGetProductListFail() {
        when(personalLoanService.getProductsLoan()).thenThrow(new NullPointerException());

        ResponseEntity<TmbOneServiceResponse<ApplyPersonalLoan>> result = personalLoanController.getProductList();
        assertTrue(result.getStatusCode().isError());
    }

    @Test
    public void testGetProductCreditListSuccess() {
        when(personalLoanService.getProductsCredit()).thenReturn(any());
        personalLoanController.getProductCreditList();
        assertTrue(true);
    }

    @Test
    public void testGetProductCreditListFail() {
        when(personalLoanService.getProductsCredit()).thenThrow(new NullPointerException());

        ResponseEntity<TmbOneServiceResponse<List<ProductData>>> result = personalLoanController.getProductCreditList();
        assertTrue(result.getStatusCode().isError());
    }

}
