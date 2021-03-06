package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInformationMapper;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.BuyFlowFirstTrade;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.productexperience.TmbErrorHandle;
import com.tmb.oneapp.productsexpservice.service.productexperience.account.EligibleDepositAccountService;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.tmb.oneapp.productsexpservice.util.ExceptionUtil.throwTmbException;
import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.successStatus;

@Service
public class OpenPortfolioValidationService extends TmbErrorHandle {

    private static final TMBLogger<OpenPortfolioValidationService> logger = new TMBLogger<>(OpenPortfolioValidationService.class);

    private CustomerServiceClient customerServiceClient;

    private CommonServiceClient commonServiceClient;

    private EligibleDepositAccountService eligibleDepositAccountService;

    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    private CustomerInformationMapper customerInformationMapper;

    private AlternativeService alternativeService;

    @Autowired
    public OpenPortfolioValidationService(
            CustomerServiceClient customerServiceClient,
            CommonServiceClient commonServiceClient,
            EligibleDepositAccountService eligibleDepositAccountService,
            OpenPortfolioActivityLogService openPortfolioActivityLogService,
            CustomerInformationMapper customerInformationMapper,
            AlternativeService alternativeService) {

        this.customerServiceClient = customerServiceClient;
        this.commonServiceClient = commonServiceClient;
        this.eligibleDepositAccountService = eligibleDepositAccountService;
        this.openPortfolioActivityLogService = openPortfolioActivityLogService;
        this.customerInformationMapper = customerInformationMapper;
        this.alternativeService = alternativeService;
    }

    /**
     * Generic Method to validate data for open portfolio
     *
     * @param correlationId                the correlation id
     * @param crmId                        the crm id
     * @param ipAddress                    the ip address
     * @param openPortfolioValidateRequest
     * @return ValidateOpenPortfolioResponse
     */
    @LogAround
    public TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateOpenPortfolioService(String correlationId, String crmId, String ipAddress,
                                                                                             OpenPortfolioValidationRequest openPortfolioValidateRequest) {

        TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfoResponse =
                    customerServiceClient.customerSearch(correlationId, crmId, CrmSearchBody.builder().searchType(ProductsExpServiceConstant.SEARCH_TYPE).searchValue(crmId).build());
            validateCustomerService(customerInfoResponse);
            CustomerSearchResponse customerInfo = customerInfoResponse.getBody().getData().get(0);

            List<DepositAccount> depositAccountList = eligibleDepositAccountService.getEligibleDepositAccounts(correlationId, crmId, false);
            validateAlternativeCase(correlationId, crmId, ipAddress, customerInfo, depositAccountList, tmbOneServiceResponse);

            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> termAndCondition = commonServiceClient.getTermAndConditionByServiceCodeAndChannel(
                    correlationId, ProductsExpServiceConstant.SERVICE_CODE_OPEN_PORTFOLIO, ProductsExpServiceConstant.CHANNEL_MOBILE_BANKING);
            if (!termAndCondition.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(termAndCondition.getBody().getData())) {
                throwTmbException("========== failed get termandcondition service ==========");
            }

            if (openPortfolioValidateRequest.isExistingCustomer()) {
                depositAccountList = null;
            }
            mappingOpenPortFolioValidationResponse(tmbOneServiceResponse, customerInfo, termAndCondition.getBody().getData(), depositAccountList);
            return tmbOneServiceResponse;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

    @LogAround
    private TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateAlternativeCase(
            String correlationId,
            String crmId,
            String ipAddress,
            CustomerSearchResponse customerInfo,
            List<DepositAccount> depositAccountList,
            TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse) {

        TmbStatus status = TmbStatusUtil.successStatus();
        tmbOneServiceResponse.setStatus(successStatus());
        tmbOneServiceResponse.setData(ValidateOpenPortfolioResponse.builder().build());

        // validate service hour
        ValidateServiceHourResponse validateServiceHourResponse = alternativeService.validateServiceHour(correlationId, status);
        BeanUtils.copyProperties(validateServiceHourResponse, tmbOneServiceResponse.getStatus());
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            tmbOneServiceResponse.getData().setServiceHour(String.format("%s-%s", validateServiceHourResponse.getStartTime(), validateServiceHourResponse.getEndTime()));
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getMessage());
            return tmbOneServiceResponse;
        }

        // validate age should > 20
        tmbOneServiceResponse.setStatus(alternativeService.validateDateNotOverTwentyYearOld(customerInfo.getBirthDate(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMessage());
            return tmbOneServiceResponse;
        }

        // validate account active only once
        tmbOneServiceResponse.setStatus(alternativeService.validateCasaAccountActiveOnce(depositAccountList, status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getMessage());
            return tmbOneServiceResponse;
        }

        // validate complete fatca form
        tmbOneServiceResponse.setStatus(alternativeService.validateFatcaFlagNotValid(customerInfo.getFatcaFlag(), status, "OPEN_PORTFOLIO"));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, tmbOneServiceResponse.getStatus().getMessage());
            return tmbOneServiceResponse;
        }

        // validate customer pass kyc (U,Blank) allow  and id card has not expired
        tmbOneServiceResponse.setStatus(alternativeService.validateKycAndIdCardExpire(customerInfo.getKycLimitedFlag(), customerInfo.getIdType(), customerInfo.getExpiryDate(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMessage());
            return tmbOneServiceResponse;
        }

        // validate customer assurance level
        tmbOneServiceResponse.setStatus(alternativeService.validateIdentityAssuranceLevel(customerInfo.getEkycIdentifyAssuranceLevel(), status, "OPEN_PORTFOLIO"));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMessage());
            return tmbOneServiceResponse;
        }

        // validate customer not us and not restricted in 30 nationality
        tmbOneServiceResponse.setStatus(alternativeService.validateNationality(correlationId, customerInfo.getNationality(), customerInfo.getNationalitySecond(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMessage());
            return tmbOneServiceResponse;
        }

        // validate customer risk level
        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(false).isFirstTrade(false).build();
        tmbOneServiceResponse.setStatus(alternativeService.validateCustomerRiskLevel(correlationId, customerInfo, status, buyFlowFirstTrade));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage());
            return tmbOneServiceResponse;
        }

        openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ipAddress, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_YES, "");
        return tmbOneServiceResponse;
    }

    private void mappingOpenPortFolioValidationResponse(TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse, CustomerSearchResponse customerInfo, TermAndConditionResponseBody termAndCondition, List<DepositAccount> depositAccountList) {
        tmbOneServiceResponse.setData(ValidateOpenPortfolioResponse.builder()
                .termsConditions(termAndCondition)
                .customerInformation(customerInformationMapper.map(customerInfo))
                .depositAccountList(depositAccountList)
                .build());
    }

    private void validateCustomerService(ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfo) throws TMBCommonException {
        if (!customerInfo.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(customerInfo.getBody().getData())) {
            throwTmbException("========== failed customer search service ==========");
        }
    }
}