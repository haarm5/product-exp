package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.information.DcaInformationDto;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.information.DcaInformationModel;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.dcainformation.DcaInformationMapper;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.*;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DcaInformationService class will get data from api services
 */
@Service
public class DcaInformationService {

    private static final TMBLogger<DcaInformationService> logger = new TMBLogger<>(DcaInformationService.class);

    private final InvestmentRequestClient investmentRequestClient;

    private final ProductsExpService productsExpService;

    private final DcaInformationMapper dcaInformationMapper;

    @Autowired
    public DcaInformationService(InvestmentRequestClient investmentRequestClient,
                                 ProductsExpService productsExpService,
                                 DcaInformationMapper dcaInformationMapper) {
        this.investmentRequestClient = investmentRequestClient;
        this.productsExpService = productsExpService;
        this.dcaInformationMapper = dcaInformationMapper;
    }


    /**
     * Method getDcaInformation to call MF Service account saving and getFundListInfo and FundSummary
     *
     * @param correlationId
     * @param crmId
     * @return TmbOneServiceResponse<DcaInformationDto>
     */
    public TmbOneServiceResponse<DcaInformationDto> getDcaInformation(String correlationId, String crmId) {

        TmbOneServiceResponse<DcaInformationDto> dcaInformationDto = new TmbOneServiceResponse<>();
        try {
            Map<String, String> headerParameter = UtilMap.createHeader(correlationId);
            List<String> portList = productsExpService.getPortList(headerParameter, crmId, false);
            UnitHolder unitHolder = new UnitHolder();
            unitHolder.setUnitHolderNumber(portList.stream().collect(Collectors.joining(",")));
            ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> fundSummaryResponse = investmentRequestClient.callInvestmentFundSummaryService(headerParameter,
                    unitHolder);
            ResponseEntity<TmbOneServiceResponse<FundListBody>> fundListBody = investmentRequestClient.callInvestmentFundListInfoService(headerParameter);
            return mappingDcaInformationDto(fundSummaryResponse, fundListBody, dcaInformationDto);
        } catch (Exception ex) {
            logger.error("error : {}", ex);
            dcaInformationDto.setStatus(null);
            dcaInformationDto.setData(null);
            return dcaInformationDto;
        }
    }

    private TmbOneServiceResponse<DcaInformationDto> mappingDcaInformationDto(ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> fundSummaryResponse, ResponseEntity<TmbOneServiceResponse<FundListBody>> fundListBody, TmbOneServiceResponse<DcaInformationDto> dcaInformationDto) {
        FundSummaryBody fundSummaryBody = fundSummaryResponse.getBody().getData().getBody();
        List<FundClass> fundClass = fundSummaryBody.getFundClassList().getFundClass();
        List<FundHouse> fundHouseList = fundClass.stream()
                .map(FundClass::getFundHouseList)
                .flatMap(Collection::stream).collect(Collectors.toList());
        List<Fund> fundList = fundHouseList.stream().map(t -> t.getFundList().getFund()).flatMap(Collection::stream).collect(Collectors.toList());
        List<FundClassListInfo> fundClassListInfos = fundListBody.getBody().getData().getFundClassList();

        List<DcaInformationModel> dcaInformationModelList = new ArrayList<>();
        for (FundClassListInfo fundClassListInfo : fundClassListInfos) {
            if (fundClassListInfo.getAllowAipFlag().equals(ProductsExpServiceConstant.APPLICATION_STATUS_FLAG_TRUE)) {
                Optional<Fund> fundOpt = fundList.stream().filter(t -> t.getFundCode().equals(fundClassListInfo.getFundCode())).findFirst();
                if (fundOpt.isPresent()) {
                    Fund fund = fundOpt.get();
                    DcaInformationModel dcaInformationModel = dcaInformationMapper.fundClassInfoToDcaInformationModel(fundClassListInfo);
                    dcaInformationModel.setMarketValue(fund.getMarketValue());
                    dcaInformationModel.setPortfolioNumber(fund.getPortfolioNumber());
                    dcaInformationModel.setUnrealizedProfit(fund.getUnrealizedProfit());
                    dcaInformationModel.setUnrealizedProfitPercent(fund.getUnrealizedProfitPercent());
                    dcaInformationModelList.add(dcaInformationModel);
                }
            }
        }
        dcaInformationDto.setStatus(TmbStatusUtil.successStatus());
        dcaInformationDto.setData(DcaInformationDto.builder().fundClassList(dcaInformationModelList).build());
        return dcaInformationDto;
    }


}
