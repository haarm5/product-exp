package com.tmb.oneapp.productsexpservice.service.productexperience.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import feign.FeignException;
import org.springframework.http.HttpStatus;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class AbstactAsyncHandleBadRequest {

    private static final TMBLogger<AbstactAsyncHandleBadRequest> logger = new TMBLogger<>(AbstactAsyncHandleBadRequest.class);

    protected void handleFeignException(FeignException feignException) throws TMBCommonException {
        logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, feignException);
        if (feignException.status() == HttpStatus.BAD_REQUEST.value()) {
            try {
                TmbOneServiceResponse<String> response = getResponseFromBadRequest(feignException);
                TmbStatus tmbStatus = response.getStatus();
                throw new TMBCommonException(
                        tmbStatus.getCode(),
                        tmbStatus.getMessage(),
                        tmbStatus.getService(),
                        HttpStatus.BAD_REQUEST,
                        null);
            } catch (JsonProcessingException e) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    <T> TmbOneServiceResponse<T> getResponseFromBadRequest(final FeignException ex)
            throws JsonProcessingException {
        TmbOneServiceResponse<T> response = new TmbOneServiceResponse<>();
        Optional<ByteBuffer> responseBody = ex.responseBody();
        if (responseBody.isPresent()) {
            ByteBuffer responseBuffer = responseBody.get();
            String responseObj = new String(responseBuffer.array(), StandardCharsets.UTF_8);
            logger.info("response msg fail {}", responseObj);
            response = ((TmbOneServiceResponse<T>) TMBUtils.convertStringToJavaObj(responseObj,
                    TmbOneServiceResponse.class));
        }
        return response;
    }

    protected TMBCommonException getTmbCommonException() {
        return new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.BAD_REQUEST,
                null);
    }
}
