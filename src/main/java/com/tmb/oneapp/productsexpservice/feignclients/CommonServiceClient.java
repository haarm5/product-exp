package com.tmb.oneapp.productsexpservice.feignclients;

import java.util.List;

import com.tmb.common.model.CommonData;
import com.tmb.oneapp.productsexpservice.model.response.NodeDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${feign.common.service.name}", url = "${feign.common.service.url}")
public interface CommonServiceClient {

	@GetMapping(value = "/apis/common/fetch/product-config")
	public ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> getProductConfig(
			@RequestHeader(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationID);


	@GetMapping(value = "/apis/common/internal/common/config")
	public ResponseEntity<TmbOneServiceResponse<List<CommonData>>> getCommonConfigByModule(
			@RequestHeader("X-Correlation-ID") String correlationId,
			@RequestParam("search")  String search);

	@GetMapping(value = "/apis/common/product/application/roadmap")
	public ResponseEntity<TmbOneServiceResponse<List<NodeDetails>>> getProductApplicationRoadMap();

}
