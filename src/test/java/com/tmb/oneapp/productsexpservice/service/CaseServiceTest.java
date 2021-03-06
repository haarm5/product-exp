package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.CaseStatusCase;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.CaseStatusResponse;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
class CaseServiceTest {

    private final CustomerServiceClient customerServiceClient = Mockito.mock(CustomerServiceClient.class);
    private final KafkaProducerService kafkaProducerService = Mockito.mock(KafkaProducerService.class);
    private final CaseService caseService = new CaseService(customerServiceClient,
            kafkaProducerService, "activityLog");

    @Test
    void getCaseStatus_firstTime_success() throws TMBCommonException {

        //getFirstTimeUsage
        TmbOneServiceResponse<CustomerFirstUsage> mockGetFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockGetFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetFirstTimeUsageResponse.setData(null);

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetFirstTimeUsageResponse));

        //postFirstTimeUsage
        TmbOneServiceResponse<String> mockPostFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockPostFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockPostFirstTimeUsageResponse.setData("1");

        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockPostFirstTimeUsageResponse));

        //getCaseStatus
        TmbOneServiceResponse<List<CaseStatusCase>> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetCaseStatusResponse.setData(Arrays.asList(
                new CaseStatusCase().setStatus("In Progress"),
                new CaseStatusCase().setStatus("In Progress"),
                new CaseStatusCase().setStatus("Closed"),
                new CaseStatusCase().setStatus("In Progress"),
                new CaseStatusCase().setStatus("Closed")
        ));

        //activityLogging
        doNothing().when(kafkaProducerService)
                .sendMessageAsync(anyString(), contains("101500201"));

        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        CaseStatusResponse response =
                caseService.getCaseStatus(header, "CST");

        assertEquals(true, response.getFirstUsageExperience());
        assertEquals("CST", response.getServiceTypeId());
        assertEquals(2, response.getCompleted().size());
        assertEquals(3, response.getInProgress().size());
        verify(kafkaProducerService, times(1)).
                sendMessageAsync(anyString(), contains("101500201"));

    }

    @Test
    void getCaseStatus_firstTime_empty() throws TMBCommonException {

        //getFirstTimeUsage
        TmbOneServiceResponse<CustomerFirstUsage> mockGetFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockGetFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetFirstTimeUsageResponse));

        //postFirstTimeUsage
        TmbOneServiceResponse<String> mockPostFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockPostFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockPostFirstTimeUsageResponse.setData("1");

        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockPostFirstTimeUsageResponse));

        //getCaseStatus
        TmbOneServiceResponse<List<CaseStatusCase>> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetCaseStatusResponse.setData(new ArrayList<>());

        //activityLogging
        doThrow(new IllegalArgumentException()).when(kafkaProducerService)
                .sendMessageAsync(anyString(), contains("101500201"));

        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        CaseStatusResponse response =
                caseService.getCaseStatus(header, "CST");

        assertEquals(true, response.getFirstUsageExperience());
        assertEquals("CST", response.getServiceTypeId());
        assertTrue(response.getCompleted().isEmpty());
        assertTrue(response.getInProgress().isEmpty());

    }

    @Test
    void getCaseStatus() {
        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenThrow(new IllegalArgumentException());

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        assertThrows(TMBCommonException.class, () -> caseService.getCaseStatus(header, "CST"));
    }

    @Test
    void getFirstTimeUsage_generalException() {
        Request request = Request.create(Request.HttpMethod.GET,
                "",
                new HashMap<>(),
                null,
                new RequestTemplate());

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenThrow(new FeignException.FeignClientException(401, "Unauthorized", request, null));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        assertThrows(TMBCommonException.class, () ->
                caseService.getFirstTimeUsage(header, "CST")
        );

    }

    @Test
    void getFirstTimeUsage_unexpectedError() {
        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenThrow(IllegalArgumentException.class);

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        assertThrows(TMBCommonException.class, () ->
                caseService.getFirstTimeUsage(header, "CST")
        );

    }

    @Test
    void getFirstTimeUsage_null() throws TMBCommonException {
        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(null));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        CustomerFirstUsage response = caseService.getFirstTimeUsage(header, "CST");

        assertNull(response);

    }

    @Test
    void getCaseStatus_null() throws TMBCommonException {
        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(null));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        List<CaseStatusCase> response = caseService.getCaseStatus(header);

        assertEquals(new ArrayList<>(), response);
    }

    @Test
    void getCaseStatus_generalException() {
        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException());

        assertThrows(TMBCommonException.class, () ->
                caseService.getCaseStatus(anyMap())
        );
    }

    @Test
    void getCaseStatus_unexpectedError() {
        Request request = Request.create(Request.HttpMethod.GET,
                "",
                new HashMap<>(),
                null,
                new RequestTemplate());

        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenThrow(new FeignException.FeignClientException(401, "Unauthorized", request, null));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        assertThrows(TMBCommonException.class, () ->
                caseService.getCaseStatus(header)
        );
    }

    @SuppressWarnings("all")
    @Test
    void mapTmbOneServiceResponse() {

        String req = "{\"status\":{\"code\":\"0009\",\"message\":\"DATA NOT FOUND\",\"service\":\"customers-service\",\"description\":null},\"data\":null}";

        ByteBuffer byteBuffer = ByteBuffer.wrap(req.getBytes(StandardCharsets.UTF_8));
        Optional<ByteBuffer> optionalByteBuffer = java.util.Optional.of(byteBuffer);

        TmbOneServiceResponse tmbOneServiceResponse = caseService.mapTmbOneServiceResponse(optionalByteBuffer);
        assertEquals("0009", tmbOneServiceResponse.getStatus().getCode());
    }

    @SuppressWarnings("all")
    @Test
    void mapTmbOneServiceResponse_notPresent() {
        Optional<ByteBuffer> optionalByteBuffer = java.util.Optional.empty();

        TmbOneServiceResponse tmbOneServiceResponse = caseService.mapTmbOneServiceResponse(optionalByteBuffer);
        assertNull(tmbOneServiceResponse);
    }

    @SuppressWarnings("all")
    @Test
    void mapTmbOneServiceResponse_badResponse() {
        String req = "badString";

        ByteBuffer byteBuffer = ByteBuffer.wrap(req.getBytes(StandardCharsets.UTF_8));
        Optional<ByteBuffer> optionalByteBuffer = java.util.Optional.of(byteBuffer);

        TmbOneServiceResponse tmbOneServiceResponse = caseService.mapTmbOneServiceResponse(optionalByteBuffer);
        assertNull(tmbOneServiceResponse);
    }

    @Test
    void asyncPostFirstTime_exception() {
        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenThrow(new IllegalArgumentException());

        caseService.asyncPostFirstTime("crmId", "deviceId", "CST");

        verify(customerServiceClient, times(1)).postFirstTimeUsage(anyString(), anyString(), eq("CST"));

    }

    @Test
    void testGetCustomerFirstUsage() {
        CustomerFirstUsage result = caseService.getCustomerFirstUsage("crmId", "deviceId");
        CustomerFirstUsage expected = new CustomerFirstUsage();
        expected.setServiceTypeId("test");
        Assertions.assertNotEquals(expected, result);
    }

    @Test
    void testGetCaseStatusCases() {
        ArrayList<CaseStatusCase> result = caseService.getCaseStatusCases("crmId");
        CaseStatusCase caseStatusCase = new CaseStatusCase();
        caseStatusCase.setStatus("1234");
        ArrayList<CaseStatusCase> expected = new ArrayList<>(Arrays.asList(caseStatusCase));
        Assertions.assertNotEquals(expected, result);
    }

}