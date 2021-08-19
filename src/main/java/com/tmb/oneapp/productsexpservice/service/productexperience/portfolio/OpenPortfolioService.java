package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.portfolio.OpenPortfolioMapper;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.request.PortfolioNicknameRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioValidationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.productexperience.account.EligibleDepositAccountService;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.tmb.oneapp.productsexpservice.util.ExceptionUtil.throwTmbException;


/**
 * OpenPortfolioService class will get data from api services, and handle business criteria
 */
@Service
public class OpenPortfolioService {

    private static final TMBLogger<OpenPortfolioService> logger = new TMBLogger<>(OpenPortfolioService.class);

    private InvestmentRequestClient investmentRequestClient;

    private EligibleDepositAccountService eligibleDepositAccountService;

    private InvestmentAsyncService investmentAsyncService;

    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    private OpenPortfolioMapper openPortfolioMapper;

    @Autowired
    public OpenPortfolioService(
            InvestmentRequestClient investmentRequestClient,
            EligibleDepositAccountService eligibleDepositAccountService,
            InvestmentAsyncService investmentAsyncService,
            OpenPortfolioActivityLogService openPortfolioActivityLogService,
            OpenPortfolioMapper openPortfolioMapper) {
        this.investmentRequestClient = investmentRequestClient;
        this.eligibleDepositAccountService = eligibleDepositAccountService;
        this.investmentAsyncService = investmentAsyncService;
        this.openPortfolioActivityLogService = openPortfolioActivityLogService;
        this.openPortfolioMapper = openPortfolioMapper;
    }

    /**
     * Method createCustomer
     *
     * @param correlationId
     * @param customerRequest
     */
    public OpenPortfolioValidationResponse createCustomer(String correlationId, String crmId, CustomerRequest customerRequest) {
        try {
            openPortfolioActivityLogService.acceptTermAndCondition(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_ACCEPT_TERM_AND_CONDITION);

            Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);
            ResponseEntity<TmbOneServiceResponse<CustomerResponseBody>> clientCustomer = investmentRequestClient.createCustomer(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), customerRequest);
            if (HttpStatus.OK.equals(clientCustomer.getStatusCode())) {
                CompletableFuture<AccountPurposeResponseBody> fetchAccountPurpose = investmentAsyncService.fetchAccountPurpose(investmentRequestHeader);
                CompletableFuture<OccupationInquiryResponseBody> occupationInquiry = investmentAsyncService.fetchOccupationInquiry(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId));
                CompletableFuture.allOf(fetchAccountPurpose, occupationInquiry);

                DepositAccount depositAccount = null;
                if (customerRequest.isExistingCustomer()) {
                    depositAccount = getDepositAccountForExisitngCustomer(investmentRequestHeader,correlationId,crmId);
                }

                return OpenPortfolioValidationResponse.builder()
                        .accountPurposeResponse(fetchAccountPurpose.get())
                        .depositAccount(depositAccount)
                        .occupationInquiryResponse(occupationInquiry.get())
                        .build();
            }
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
        }
        return null;
    }

    private DepositAccount getDepositAccountForExisitngCustomer(Map<String, String> investmentRequestHeader,String correlationId, String crmId) throws TMBCommonException {
        ResponseEntity<TmbOneServiceResponse<AccountRedeemResponseBody>> fetchAccountRedeem = investmentRequestClient.getCustomerAccountRedeem(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId));
        AccountRedeemResponseBody accountRedeem = fetchAccountRedeem.getBody().getData();
        List<DepositAccount> eligibleDepositAccounts = eligibleDepositAccountService.getEligibleDepositAccounts(correlationId, accountRedeem.getCrmId());
        Optional<DepositAccount> account = eligibleDepositAccounts.stream()
                .filter(depositAccount -> accountRedeem.getAccountRedeem().equals(depositAccount.getAccountNumber()))
                .findFirst();
        if (account.isEmpty()) {
            throwTmbException("========== failed account return 0 in list for exisitng user ==========");
        }
        return account.get();
    }

    /**
     * Method openPortfolio
     *
     * @param correlationId
     * @param openPortfolioRequestBody
     */
    public PortfolioResponse openPortfolio(String correlationId, String crmId, OpenPortfolioRequestBody openPortfolioRequestBody) {
        OccupationResponseBody occupationResponseBody = null;
        Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);

        try {
            RelationshipRequest relationshipRequest = openPortfolioMapper.openPortfolioRequestBodyToRelationshipRequest(openPortfolioRequestBody);
            CompletableFuture<RelationshipResponseBody> relationship = investmentAsyncService.updateClientRelationship(
                    investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), relationshipRequest);

            OpenPortfolioRequest openPortfolioRequest = openPortfolioMapper.openPortfolioRequestBodyToOpenPortfolioRequest(openPortfolioRequestBody);
            CompletableFuture<OpenPortfolioResponseBody> openPortfolio = investmentAsyncService.openPortfolio(
                    investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), openPortfolioRequest);

            if (openPortfolioRequestBody.getOccupationRequest() == null) {
                CompletableFuture.allOf(relationship, openPortfolio);
            } else {
                CompletableFuture<OccupationResponseBody> occupation = investmentAsyncService.updateOccupation(
                        investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), openPortfolioRequestBody.getOccupationRequest());
                CompletableFuture.allOf(relationship, openPortfolio, occupation);
                occupationResponseBody = occupation.get();
            }

            openPortfolioActivityLogService.enterCorrectPin(correlationId, crmId, ProductsExpServiceConstant.SUCCESS, openPortfolio.get().getPortfolioNumber(), openPortfolioRequestBody.getPortfolioNickName());

            OpenPortfolioResponseBody openPortfolioResponseBody = openPortfolio.get();
            PortfolioNicknameRequest portfolioNicknameRequest = PortfolioNicknameRequest.builder()
                    .portfolioNumber(openPortfolioResponseBody.getPortfolioNumber())
                    .portfolioNickName(openPortfolioRequestBody.getPortfolioNickName())
                    .build();
            ResponseEntity<TmbOneServiceResponse<PortfolioNicknameResponseBody>> portfolioNickname = investmentRequestClient.updatePortfolioNickname(investmentRequestHeader, portfolioNicknameRequest);

            return PortfolioResponse.builder()
                    .relationshipResponse(relationship.get())
                    .openPortfolioResponse(openPortfolio.get())
                    .portfolioNicknameResponse(portfolioNickname.getBody().getData())
                    .occupationResponse(occupationResponseBody != null ? occupationResponseBody : null)
                    .build();
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            openPortfolioActivityLogService.enterCorrectPin(correlationId, crmId, ProductsExpServiceConstant.FAILED, "", openPortfolioRequestBody.getPortfolioNickName());
            return null;
        }
    }
}
