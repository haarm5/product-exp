package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;

public class TmbStatusUtil {

    private TmbStatusUtil() {
    }

    public static TmbStatus successStatus() {
        TmbStatus status = new TmbStatus();
        status.setCode(ProductsExpServiceConstant.SUCCESS_CODE);
        status.setDescription(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        status.setMessage(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        return status;
    }

    public static TmbStatus failedStatus() {
        TmbStatus status = new TmbStatus();
        status.setCode(ResponseCode.FAILED.getCode());
        status.setDescription(ResponseCode.FAILED.getDesc());
        status.setMessage(ResponseCode.FAILED.getMessage());
        status.setService(ResponseCode.FAILED.getService());
        return status;
    }

    public static TmbStatus notFoundStatus() {
        TmbStatus status = new TmbStatus();
        status.setCode(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE);
        status.setDescription(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        status.setMessage(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        return status;
    }

    public static TmbStatus badRequestStatus() {
        TmbStatus status = new TmbStatus();
        status.setCode(ProductsExpServiceConstant.BAD_REQUEST_CODE);
        status.setDescription(ProductsExpServiceConstant.BAD_REQUEST_MESSAGE);
        status.setMessage(ProductsExpServiceConstant.BAD_REQUEST_MESSAGE);
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        return status;
    }
}
