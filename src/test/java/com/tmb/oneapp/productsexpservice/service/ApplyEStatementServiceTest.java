package com.tmb.oneapp.productsexpservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.Customer;
import com.tmb.common.model.StatementFlag;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.creditcard.CardStatus;
import com.tmb.common.model.creditcard.CreditCardDetail;
import com.tmb.common.model.creditcard.GetCardsBalancesResponse;
import com.tmb.common.model.creditcard.UpdateEStatmentResp;
import com.tmb.common.model.customer.UpdateEStatmentRequest;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.LoanAccount;
import com.tmb.oneapp.productsexpservice.model.ProductHoldingsResp;

@RunWith(JUnit4.class)
public class ApplyEStatementServiceTest {

	@Mock
	private CustomerServiceClient customerServiceClient;
	@Mock
	ApplyEStatementService applyEStatementService;
	@Mock
	CreditCardClient creditCardClient;
	@Mock
	AccountRequestClient accountReqClient;
	@Mock
	CreditCardLogService activitylogService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		applyEStatementService = new ApplyEStatementService(customerServiceClient, creditCardClient, accountReqClient,
				activitylogService);
	}

	@Test
	void testGetEStatement() throws Exception {
		String correlationId = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
		String crmId = ProductsExpServiceConstant.X_CRMID;
		TmbOneServiceResponse<UpdateEStatmentResp> oneServiceResponse = new TmbOneServiceResponse<>();
		UpdateEStatmentResp data = new UpdateEStatmentResp();
		oneServiceResponse.setData(data);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerEStatement(any(), any()))
				.thenReturn(ResponseEntity.ok(oneServiceResponse));
		applyEStatementService.getEStatement(crmId, correlationId);
		Assert.assertNotNull(ResponseEntity.ok(oneServiceResponse));
	}

	@Test
	void testUpdateEStatementProductGroupLoan() throws Exception {
		String correlationId = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
		String crmId = ProductsExpServiceConstant.X_CRMID;
		UpdateEStatmentRequest updateEstatementReq = new UpdateEStatmentRequest();

		TmbOneServiceResponse<UpdateEStatmentResp> oneServiceResponse = new TmbOneServiceResponse<>();
		UpdateEStatmentResp data = new UpdateEStatmentResp();
		Customer customer = new Customer();
		StatementFlag statementFlag = new StatementFlag();
		customer.setStatementFlag(statementFlag);
		data.setCustomer(customer);
		oneServiceResponse.setData(data);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerEStatement(any(), any()))
				.thenReturn(ResponseEntity.ok(oneServiceResponse));

		TmbOneServiceResponse<ProductHoldingsResp> accountResponse = new TmbOneServiceResponse<>();
		ProductHoldingsResp productHoldingsResp = new ProductHoldingsResp();
		List<LoanAccount> loanAccounts = new ArrayList();
		LoanAccount acc = new LoanAccount();
		acc.setAccountName("5213323");
		loanAccounts.add(acc);
		productHoldingsResp.setLoanAccounts(loanAccounts);
		accountResponse.setData(productHoldingsResp);
		accountResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(accountReqClient.getProductHoldingService(any(), any())).thenReturn(ResponseEntity.ok(accountResponse));

		GetCardsBalancesResponse cardsBalancesResponse = new GetCardsBalancesResponse();
		List<CreditCardDetail> creditCard = new ArrayList<CreditCardDetail>();
		CreditCardDetail cd = new CreditCardDetail();
		cd.setAccountId("5213323");
		creditCard.add(cd);
		cardsBalancesResponse.setCreditCard(creditCard);
		when(creditCardClient.getCreditCardBalance(any(), any())).thenReturn(ResponseEntity.ok(cardsBalancesResponse));

		TmbOneServiceResponse<UpdateEStatmentResp> updateServiceResponse = new TmbOneServiceResponse<>();
		updateServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.updateEStatement(any(), any())).thenReturn(ResponseEntity.ok(updateServiceResponse));
		
		applyEStatementService.updateEstatement(crmId, correlationId, updateEstatementReq,new HashMap());
		Assert.assertNotNull(ResponseEntity.ok(oneServiceResponse));
	}

	@Test
	void testUpdateEStatementProductGroupCreditCard() throws Exception {
		String correlationId = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
		String crmId = ProductsExpServiceConstant.X_CRMID;
		UpdateEStatmentRequest updateEstatementReq = new UpdateEStatmentRequest();
		updateEstatementReq.setAccountId("5213323");
		TmbOneServiceResponse<UpdateEStatmentResp> oneServiceResponse = new TmbOneServiceResponse<>();
		UpdateEStatmentResp data = new UpdateEStatmentResp();
		Customer customer = new Customer();
		StatementFlag statementFlag = new StatementFlag();
		customer.setStatementFlag(statementFlag);
		data.setCustomer(customer);
		oneServiceResponse.setData(data);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerEStatement(any(), any()))
				.thenReturn(ResponseEntity.ok(oneServiceResponse));

		TmbOneServiceResponse<ProductHoldingsResp> accountResponse = new TmbOneServiceResponse<>();
		ProductHoldingsResp productHoldingsResp = new ProductHoldingsResp();
		List<LoanAccount> loanAccounts = new ArrayList();
		productHoldingsResp.setLoanAccounts(loanAccounts);
		accountResponse.setData(productHoldingsResp);
		accountResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(accountReqClient.getProductHoldingService(any(), any())).thenReturn(ResponseEntity.ok(accountResponse));

		GetCardsBalancesResponse cardsBalancesResponse = new GetCardsBalancesResponse();
		List<CreditCardDetail> creditCard = new ArrayList<CreditCardDetail>();
		CreditCardDetail cd = new CreditCardDetail();
		CardStatus cardStatus = new CardStatus();
		cardStatus.setCardPloanFlag("1");
		cd.setAccountId("5213323");
		cd.setCardStatus(cardStatus);
		creditCard.add(cd);
		cardsBalancesResponse.setCreditCard(creditCard);
		when(creditCardClient.getCreditCardBalance(any(), any())).thenReturn(ResponseEntity.ok(cardsBalancesResponse));
		when(creditCardClient.updateEmailEStatement(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
		when(creditCardClient.updateEnableEStatement(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
		TmbOneServiceResponse<UpdateEStatmentResp> updateServiceResponse = new TmbOneServiceResponse<>();
		updateServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.updateEStatement(any(), any())).thenReturn(ResponseEntity.ok(updateServiceResponse));

		applyEStatementService.updateEstatement(crmId, correlationId, updateEstatementReq,new HashMap());
		Assert.assertNotNull(ResponseEntity.ok(oneServiceResponse));
	}

	@Test
	void testUpdateEStatementProductGroupFlashCard() throws Exception {
		String correlationId = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
		String crmId = ProductsExpServiceConstant.X_CRMID;
		UpdateEStatmentRequest updateEstatementReq = new UpdateEStatmentRequest();
		updateEstatementReq.setAccountId("5213323");
		TmbOneServiceResponse<UpdateEStatmentResp> oneServiceResponse = new TmbOneServiceResponse<>();
		UpdateEStatmentResp data = new UpdateEStatmentResp();
		Customer customer = new Customer();
		StatementFlag statementFlag = new StatementFlag();
		customer.setStatementFlag(statementFlag);
		data.setCustomer(customer);
		oneServiceResponse.setData(data);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerEStatement(any(), any()))
				.thenReturn(ResponseEntity.ok(oneServiceResponse));

		TmbOneServiceResponse<ProductHoldingsResp> accountResponse = new TmbOneServiceResponse<>();
		ProductHoldingsResp productHoldingsResp = new ProductHoldingsResp();
		List<LoanAccount> loanAccounts = new ArrayList();
		productHoldingsResp.setLoanAccounts(loanAccounts);
		accountResponse.setData(productHoldingsResp);
		accountResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(accountReqClient.getProductHoldingService(any(), any())).thenReturn(ResponseEntity.ok(accountResponse));

		GetCardsBalancesResponse cardsBalancesResponse = new GetCardsBalancesResponse();
		List<CreditCardDetail> creditCard = new ArrayList<CreditCardDetail>();
		CreditCardDetail cd = new CreditCardDetail();
		CardStatus cardStatus = new CardStatus();
		cardStatus.setCardPloanFlag("2");
		cd.setAccountId("5213323");
		cd.setCardStatus(cardStatus);
		creditCard.add(cd);
		cardsBalancesResponse.setCreditCard(creditCard);
		when(creditCardClient.getCreditCardBalance(any(), any())).thenReturn(ResponseEntity.ok(cardsBalancesResponse));
		
		when(creditCardClient.updateEmailEStatement(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
		when(creditCardClient.updateEnableEStatement(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
		
		TmbOneServiceResponse<UpdateEStatmentResp> updateServiceResponse = new TmbOneServiceResponse<>();
		updateServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.updateEStatement(any(), any())).thenReturn(ResponseEntity.ok(updateServiceResponse));
		

		applyEStatementService.updateEstatement(crmId, correlationId, updateEstatementReq,new HashMap());
		Assert.assertNotNull(ResponseEntity.ok(oneServiceResponse));
	}

	@Test
	void testGetEmailStatementFlagProductGroupFlashCard() throws Exception {
		String correlationId = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
		String crmId = ProductsExpServiceConstant.X_CRMID;
		String accountId = "5213323";
		UpdateEStatmentResp data = new UpdateEStatmentResp();
		Customer customer = new Customer();
		StatementFlag statementFlag = new StatementFlag();
		statementFlag.setEReadyCashStatementFlag("Y");
		customer.setStatementFlag(statementFlag);
		data.setCustomer(customer);

		TmbOneServiceResponse<ProductHoldingsResp> accountResponse = new TmbOneServiceResponse<>();
		ProductHoldingsResp productHoldingsResp = new ProductHoldingsResp();
		List<LoanAccount> loanAccounts = new ArrayList();
		productHoldingsResp.setLoanAccounts(loanAccounts);
		accountResponse.setData(productHoldingsResp);
		accountResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(accountReqClient.getProductHoldingService(any(), any())).thenReturn(ResponseEntity.ok(accountResponse));

		GetCardsBalancesResponse cardsBalancesResponse = new GetCardsBalancesResponse();
		List<CreditCardDetail> creditCard = new ArrayList<CreditCardDetail>();
		CreditCardDetail cd = new CreditCardDetail();
		CardStatus cardStatus = new CardStatus();
		cardStatus.setCardPloanFlag("2");
		cd.setAccountId("5213323");
		cd.setCardStatus(cardStatus);
		creditCard.add(cd);
		cardsBalancesResponse.setCreditCard(creditCard);
		when(creditCardClient.getCreditCardBalance(any(), any())).thenReturn(ResponseEntity.ok(cardsBalancesResponse));

		String result = applyEStatementService.getEmailStatementFlag(crmId, correlationId, accountId, data);
		Assert.assertEquals("Y", result);
	}

	@Test
	void testGetEmailStatementFlagProductGroupCreditCard() throws Exception {
		String correlationId = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
		String crmId = ProductsExpServiceConstant.X_CRMID;
		String accountId = "5213323";
		UpdateEStatmentResp data = new UpdateEStatmentResp();
		Customer customer = new Customer();
		StatementFlag statementFlag = new StatementFlag();
		statementFlag.setECreditcardStatementFlag("Y");
		customer.setStatementFlag(statementFlag);
		data.setCustomer(customer);

		TmbOneServiceResponse<ProductHoldingsResp> accountResponse = new TmbOneServiceResponse<>();
		ProductHoldingsResp productHoldingsResp = new ProductHoldingsResp();
		List<LoanAccount> loanAccounts = new ArrayList();
		productHoldingsResp.setLoanAccounts(loanAccounts);
		accountResponse.setData(productHoldingsResp);
		accountResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(accountReqClient.getProductHoldingService(any(), any())).thenReturn(ResponseEntity.ok(accountResponse));

		GetCardsBalancesResponse cardsBalancesResponse = new GetCardsBalancesResponse();
		List<CreditCardDetail> creditCard = new ArrayList<CreditCardDetail>();
		CreditCardDetail cd = new CreditCardDetail();
		CardStatus cardStatus = new CardStatus();
		cardStatus.setCardPloanFlag("1");
		cd.setAccountId("5213323");
		cd.setCardStatus(cardStatus);
		creditCard.add(cd);
		cardsBalancesResponse.setCreditCard(creditCard);
		when(creditCardClient.getCreditCardBalance(any(), any())).thenReturn(ResponseEntity.ok(cardsBalancesResponse));

		String result = applyEStatementService.getEmailStatementFlag(crmId, correlationId, accountId, data);
		Assert.assertEquals("Y", result);
	}

	@Test
	void testGetEmailStatementFlagProductGroupLoan() throws Exception {
		String correlationId = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
		String crmId = ProductsExpServiceConstant.X_CRMID;
		String accountId = "5213323";
		UpdateEStatmentResp data = new UpdateEStatmentResp();
		Customer customer = new Customer();
		StatementFlag statementFlag = new StatementFlag();
		statementFlag.setECashToGoStatementFlag("Y");
		customer.setStatementFlag(statementFlag);
		data.setCustomer(customer);

		TmbOneServiceResponse<ProductHoldingsResp> accountResponse = new TmbOneServiceResponse<>();
		ProductHoldingsResp productHoldingsResp = new ProductHoldingsResp();
		List<LoanAccount> loanAccounts = new ArrayList();
		LoanAccount acc = new LoanAccount();
		acc.setAccountName("5213323");
		loanAccounts.add(acc);
		productHoldingsResp.setLoanAccounts(loanAccounts);
		accountResponse.setData(productHoldingsResp);
		accountResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(accountReqClient.getProductHoldingService(any(), any())).thenReturn(ResponseEntity.ok(accountResponse));

		GetCardsBalancesResponse cardsBalancesResponse = new GetCardsBalancesResponse();
		List<CreditCardDetail> creditCard = new ArrayList<CreditCardDetail>();
		CreditCardDetail cd = new CreditCardDetail();
		CardStatus cardStatus = new CardStatus();
		cardStatus.setCardPloanFlag("1");
		cd.setAccountId("5213324");
		cd.setCardStatus(cardStatus);
		creditCard.add(cd);
		cardsBalancesResponse.setCreditCard(creditCard);
		when(creditCardClient.getCreditCardBalance(any(), any())).thenReturn(ResponseEntity.ok(cardsBalancesResponse));

		String result = applyEStatementService.getEmailStatementFlag(crmId, correlationId, accountId, data);
		Assert.assertEquals("Y", result);
	}

}
