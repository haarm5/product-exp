package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.enums.DcaValidationErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DcaInformationService class will validate for dca fund and get fund fact sheet
 */
@Service
public class DcaValidationService {

    private static final TMBLogger<DcaValidationService> logger = new TMBLogger<>(DcaValidationService.class);

    private InvestmentRequestClient investmentRequestClient;

    public DcaValidationService(InvestmentRequestClient investmentRequestClient) {
        this.investmentRequestClient = investmentRequestClient;
    }

    /**
     * Method dcaValidation to call MF Service account saving and fund rule and fund fact sheet
     *
     * @param correlationId
     * @param crmId
     * @param dcaValidationRequest
     * @return TmbOneServiceResponse<DcaInformationDto>
     */
    public TmbOneServiceResponse<DcaValidationDto> dcaValidation(String correlationId, String crmId, DcaValidationRequest dcaValidationRequest) {

        TmbOneServiceResponse<DcaValidationDto> dcaValidationDtoTmbOneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = TmbStatusUtil.successStatus();
        dcaValidationDtoTmbOneServiceResponse.setStatus(tmbStatus);

        try{
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);

            tmbStatus = validatePtesPort(crmId,dcaValidationRequest, invHeaderReqParameter,dcaValidationDtoTmbOneServiceResponse.getStatus());
            if(!tmbStatus.getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)){
                return dcaValidationDtoTmbOneServiceResponse;
            }

            ResponseEntity<TmbOneServiceResponse<FundRuleBody>> fundRule = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter,FundRuleRequestBody.builder()
                    .fundCode(dcaValidationRequest.getFundCode())
                    .fundHouseCode(dcaValidationRequest.getFundHouseCode())
                    .tranType(dcaValidationRequest.getTranType())
                    .build());
            tmbStatus = validateAllowAipFlag(fundRule,dcaValidationDtoTmbOneServiceResponse.getStatus());
            if(!tmbStatus.getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)){
                return dcaValidationDtoTmbOneServiceResponse;
            }

            ResponseEntity<TmbOneServiceResponse<FfsResponse>> fundFactSheet = investmentRequestClient.callInvestmentFundFactSheetService(invHeaderReqParameter, FfsRequestBody.builder().fundCode(dcaValidationRequest.getFundCode()).language(dcaValidationRequest.getLanguage()).build());
            dcaValidationDtoTmbOneServiceResponse.setData(DcaValidationDto.builder().factsheetData(fundFactSheet.getBody().getData().getBody().getFactSheetData()).build());
           return dcaValidationDtoTmbOneServiceResponse;
        }catch (Exception ex){
            logger.error("error : {}",ex);
            dcaValidationDtoTmbOneServiceResponse.setStatus(null);
            dcaValidationDtoTmbOneServiceResponse.setData(null);
            return dcaValidationDtoTmbOneServiceResponse;
        }
    }

    private TmbStatus validateAllowAipFlag(ResponseEntity<TmbOneServiceResponse<FundRuleBody>> fundRule,TmbStatus tmbStatus) throws TMBCommonException {
        if(!fundRule.getStatusCode().equals(HttpStatus.OK)){
            throw new TMBCommonException("failed fetch fund rule");
        }
        FundRuleInfoList fundRuleInfoList = fundRule.getBody().getData().getFundRuleInfoList().get(0);
        if(!fundRuleInfoList.getAllowAipFlag().equals(ProductsExpServiceConstant.APPLICATION_STATUS_FLAG_TRUE)){
            tmbStatus.setCode(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getCode());
            tmbStatus.setMessage(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getMsg());
            tmbStatus.setDescription(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getDesc());
            return tmbStatus;
        }
        return tmbStatus;
    }

    private TmbStatus validatePtesPort(String crmId, DcaValidationRequest dcaValidationRequest, Map<String, String> invHeaderReqParameter, TmbStatus tmbStatus) {
        try{
            ResponseEntity<TmbOneServiceResponse<List<PtesDetail>>> ptesPort = investmentRequestClient.getPtesPort(invHeaderReqParameter,crmId);
            List<PtesDetail> ptesPortList = ptesPort.getBody().getData();
            Optional<PtesDetail> ptesPortOptional = ptesPortList.stream()
                    .filter(t -> t.getPortfolioFlag().equals(ProductsExpServiceConstant.PTES_PORT_FOLIO_FLAG) &&
                            t.getPortfolioNumber().equals(dcaValidationRequest.getPortfolioNumber()))
                    .findFirst();
            if(ptesPortOptional.isPresent()){
                tmbStatus.setCode(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getCode());
                tmbStatus.setMessage(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getMsg());
                tmbStatus.setDescription(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getDesc());
                return tmbStatus;
            }

        }catch (Exception ex){
            logger.info("Fetch Ptes Failed");
        }
        return tmbStatus;
    }

}