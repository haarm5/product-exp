package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.FundSummaryByPortBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.request.OccupationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.request.CountToBeProcessOrderRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.response.CountOrderProcessingResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.request.FirstTradeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.processfirsttrade.ProcessFirstTradeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.request.FundAllocationRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.response.FundAllocationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.request.PortfolioNicknameRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.saveordercreation.SaveOrderCreationRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.request.OrderAIPRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.response.OrderAIPResponseBody;
import com.tmb.oneapp.productsexpservice.model.request.fundfactsheet.FundFactSheetRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetData;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustomerFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * InvestmentClient interface consume account details from investment service
 */
@FeignClient(name = "${investment.service.name}", url = "${investment.service.url}")
public interface InvestmentRequestClient {

    /**
     * Call investment fund rule service response entity.
     *
     * @param headers             the headers
     * @param fundRuleRequestBody the fund rule request body
     * @return response entity
     */
    @PostMapping(value = "${investment.service.fund.rule.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundRuleResponse>> callInvestmentFundRuleService(
            @RequestHeader Map<String, String> headers,
            @RequestBody FundRuleRequestBody fundRuleRequestBody);

    /**
     * Call investment fund acc detail service response entity.
     *
     * @param headers                the headers
     * @param fundAccountRequestBody the fund account request body
     * @return response entity
     */
    @PostMapping(value = "${investment.service.account.detail.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<AccountDetailResponse>> callInvestmentFundAccountDetailService(
            @RequestHeader Map<String, String> headers, @RequestBody FundAccountRequestBody fundAccountRequestBody);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers    the headersOrderRequestClient
     * @param unitHolder the unit holder
     * @return the fund summary response
     */
    @PostMapping(value = "${investment.service.fund.summary.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundSummaryBody>> callInvestmentFundSummaryService(
            @RequestHeader Map<String, String> headers, @RequestBody UnitHolder unitHolder);

    /***
     * Call investment to get fund summary by port
     * @param headers
     * @param unitHolder
     * @return
     */
    @PostMapping(value = "${investment.service.fund.summary.by.port.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundSummaryByPortBody>> callInvestmentFundSummaryByPortService(
            @RequestHeader Map<String, String> headers, @RequestBody UnitHolder unitHolder);

    /***
     * Call investment to get fund sumaary by port
     * @param headers
     * @param crmId
     * @param countToBeProcessOrderRequestBody
     * @return
     */
    @PostMapping(value = "${investment.service.fund.processed.order.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<CountOrderProcessingResponseBody>> callInvestmentCountProcessOrderService(
            @RequestHeader Map<String, String> headers,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @RequestBody CountToBeProcessOrderRequestBody countToBeProcessOrderRequestBody);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers  the headers
     * @param fundCode the fund code
     * @return the fund summary response
     */
    @GetMapping(value = "${investment.service.fund.holiday.url}")
    ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> callInvestmentFundHolidayService(
            @RequestHeader Map<String, String> headers, @PathVariable("fundCode") String fundCode);

    /**
     * Call investment fund listInfo service fund summary response.
     *
     * @param headers the headers
     * @return the fund summary response
     */
    @PostMapping(value = "${investment.service.fund.listinfo.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundListBody>> callInvestmentFundListInfoService(
            @RequestHeader Map<String, String> headers);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers                  the headers
     * @param fundFactSheetRequestBody the unit holder
     * @return the fund fact sheet response
     */
    @PostMapping(value = "${investment.service.fund.fact.sheet.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundFactSheetData>> callInvestmentFundFactSheetService(
            @RequestHeader Map<String, String> headers, @RequestBody FundFactSheetRequestBody fundFactSheetRequestBody);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers the headers
     * @param crmId   the crm id
     * @return the Suitability response
     */
    @PostMapping(value = "${investment.service.fund.suitability.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<SuitabilityInfo>> callInvestmentFundSuitabilityService(
            @RequestHeader Map<String, String> headers,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers                the headers
     * @param orderStmtByPortRequest the rmID
     * @return the Suitability response
     */
    @PostMapping(value = "${investment.service.fund.statement.by.port.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<StatementResponse>> callInvestmentStatementByPortService(
            @RequestHeader Map<String, String> headers, @RequestBody OrderStmtByPortRequest orderStmtByPortRequest);

    /**
     * Call investment fund favorite service fund favorite response.
     *
     * @param headers the headers
     * @return the fund favorite response
     */
    @PostMapping(value = "${investment.service.fund.list.favorite.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<List<CustomerFavoriteFundData>>> callInvestmentFundFavoriteService(
            @RequestHeader Map<String, String> headers);

    /**
     * Call investment to get ptest port
     *
     * @param headers
     * @param crmId
     * @return
     */
    @GetMapping(value = "${investment.service.fund.get.ptes.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<List<PtesDetail>>> getPtesPort(
            @RequestHeader Map<String, String> headers,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId);

    /**
     * Call investment fund information service to get fund information response.
     *
     * @param header the headers
     * @return the fund information response
     */
    @PostMapping(value = "${investment.service.fund.information.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<InformationBody>> getFundInformation(
            @RequestHeader Map<String, String> header, @RequestBody FundCodeRequestBody fundCodeRequestBody);

    /**
     * Call investment fund daily nav service to get fund daily nav response.
     *
     * @param header the headers
     * @return the fund daily nav response
     */
    @PostMapping(value = "${investment.service.fund.daily.nav.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<DailyNavBody>> getFundDailyNav(
            @RequestHeader Map<String, String> header, @RequestBody FundCodeRequestBody fundCodeRequestBody);

    /**
     * Call investment fund allocation service to get fund allocation response.
     *
     * @param header the headers
     * @return the fund allocation response
     */
    @PostMapping(value = "${investment.service.fund.allocation.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundAllocationResponse>> callInvestmentFundAllocation(
            @RequestHeader Map<String, String> header, @RequestBody FundAllocationRequestBody fundAllocationRequestBody);

    /**
     * Call investment fund allocation service to get fund allocation response.
     *
     * @param header the headers
     * @return the fund allocation response
     */
    @PostMapping(value = "${investment.service.customer.create.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<CustomerResponseBody>> createCustomer(
            @RequestHeader Map<String, String> header,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @RequestBody CustomerRequest customerRequest);

    /**
     * Call investment account purpose service to get account purpose response.
     *
     * @param header the headers
     * @return the account purpose response
     */
    @PostMapping(value = "${investment.service.customer.account.purpose.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<AccountPurposeResponseBody>> getCustomerAccountPurpose(
            @RequestHeader Map<String, String> header);

    /**
     * Call investment account redeem service to get account redeem response.
     *
     * @param header the headers
     * @param crmId  the crm id
     * @return the account redeem response
     */
    @PostMapping(value = "${investment.service.customer.account.redeem.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<AccountRedeemResponseBody>> getCustomerAccountRedeem(
            @RequestHeader Map<String, String> header,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId);

    /**
     * Call investment occupation inquiry service to get occupation inquiry response.
     *
     * @param header the headers
     * @param crmId  the crm id
     * @return the occupation inquiry response
     */
    @PostMapping(value = "${investment.service.customer.occupation.inquiry.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<OccupationInquiryResponseBody>> getCustomerOccupationInquiry(
            @RequestHeader Map<String, String> header,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId);


    /**
     * Call investment first trade inquiry service to get first trade response.
     *
     * @param header the headers
     * @param firstTradeRequestBody  the body request
     * @return the first trade inquiry response
     */
    @PostMapping(value = "${investment.service.fund.first.trade.inquiry.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FirstTradeResponseBody>> getFirstTrade(
            @RequestHeader Map<String, String> header,
            @RequestBody FirstTradeRequestBody firstTradeRequestBody);

    /**
     * Call investment for process first trade inquiry service to get process first trade response.
     *
     * @param header the headers
     * @param processFirstTradeRequestBody  the body request
     * @return the first trade inquiry response
     */
    @PostMapping(value = "${investment.service.fund.processed.first.trade}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<String>> processFirstTrade(
            @RequestHeader Map<String, String> header,
            @RequestBody ProcessFirstTradeRequestBody processFirstTradeRequestBody);

    /**
     * Call investment for save order payment service.
     *
     * @param header the headers
     * @param saveOrderCreationRequestBody  the body request
     * @return the first trade inquiry response
     */
    @PostMapping(value = "${investment.service.fund.transaction.save.order.creation.payment}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<String>> saveOrderPayment(
            @RequestHeader Map<String, String> header,
            @RequestBody SaveOrderCreationRequestBody saveOrderCreationRequestBody);

    /**
     * Call investment client relationship service to update client relationship.
     *
     * @param header              the headers
     * @param crmId               the crmId
     * @param relationshipRequest the relationshipRequest
     * @return the client relationship response
     */
    @PostMapping(value = "${investment.service.client.relationship.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<RelationshipResponseBody>> updateClientRelationship(
            @RequestHeader Map<String, String> header,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @RequestBody RelationshipRequest relationshipRequest);

    /**
     * Call investment open portfolio service to open portfolio.
     *
     * @param header               the headers
     * @param crmId                the crmId
     * @param openPortfolioRequest the openPortfolioRequest
     * @return the open portfolio response
     */
    @PostMapping(value = "${investment.service.open.portfolio.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<OpenPortfolioResponseBody>> openPortfolio(
            @RequestHeader Map<String, String> header,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @RequestBody OpenPortfolioRequest openPortfolioRequest);

    /**
     * Call investment update occupation service to update customer occupation.
     *
     * @param header            the headers
     * @param crmId             the crmId
     * @param occupationRequest the occupationRequest
     * @return the open portfolio response
     */
    @PostMapping(value = "${investment.service.occupation.update.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<OccupationResponseBody>> updateOccupation(
            @RequestHeader Map<String, String> header,
            @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @RequestBody OccupationRequest occupationRequest);

    /**
     * Call investment portfolio nickname service to create or update portfolio nickname.
     *
     * @param header                   the headers
     * @param portfolioNicknameRequest the portfolioNicknameRequest
     * @return the portfolio nickname response
     */
    @PostMapping(value = "${investment.service.portfolio.nickname.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<PortfolioNicknameResponseBody>> updatePortfolioNickname(
            @RequestHeader Map<String, String> header, @RequestBody PortfolioNicknameRequest portfolioNicknameRequest);

    /**
     * Call investment fund ListFundInfo service fund list response.
     *
     * @param headers the headers
     * @return the FundListBody response
     */
    @PostMapping(value = "${investment.service.fund.listinfo.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundListBySuitScoreBody>> callInvestmentListFundInfoService(@RequestHeader Map<String, String> headers);


    /**
     * Call investment order creation service.
     *
     * @param header the headers
     * @param orderCreationPaymentRequestBody the body
     * @return the OrderCreationPaymentResponse response
     */
    @PostMapping(value = "${investment.service.fund.transaction.order.creation.payment}")
    ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> createOrderPayment(
            @RequestHeader Map<String, String> header,
            @RequestBody OrderCreationPaymentRequestBody orderCreationPaymentRequestBody);


    /**
     * Call investment create aip order service.
     *
     * @param header the headers
     * @param orderAIPRequestBody the body
     * @return the OrderAIPResponseBody response
     */
    @PostMapping(value = "${investment.service.fund.transaction.create.aip.order}")
    ResponseEntity<TmbOneServiceResponse<OrderAIPResponseBody>> createAipOrder(
            @RequestHeader Map<String, String> header,
            @RequestBody OrderAIPRequestBody orderAIPRequestBody);


}
