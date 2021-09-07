package com.tmb.oneapp.productsexpservice.model.response.lending;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Calendar;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EAppResponse {
    String productNameTh;
    String productType;
    String appNo;
    String employmentStatus;
    BigDecimal salary;
    BigDecimal otherIncome;
    BigDecimal limitApplied;
    BigDecimal monthlyInstallment;
    BigDecimal tenure;
    BigDecimal requestAmount;
    BigDecimal interest;
    String disburstAccountNo;
    String paymentPlan;
    String paymentMethod;
    String paymentAccountName;
    String paymentAccountNo;
    String paymentCriteria;
    String loanWithOtherBank;
    String considerLoanWithOtherBank;

    String idType;
    String idNo;
    String issueCountry;
    Calendar issueDate;
    Calendar expiryDate;
    String nameTh;
    String nameEn;
    Calendar birthDay;
    String mobileNo;
    String educationLevel;
    String sourceFromCountry;
    String nationality;
    String maritalStatus;
    String placeOfBirth;
    String email;
    String contactAddress;
    String residentStatus;

    String rmOccupation;
    String occupation;
    String businessType;
    String businessSubType;
    String contractType;
    String workPeriodYear;
    String workPeriodMonth;
    String workName;
    String workAddress;
    String workTel;
    String workTelEx;
    String incomeBank;
    String incomeBankAccountNo;
    BigDecimal cashFlow;
    BigDecimal sharePercent;
    String eStatement;
    String delivery;
    String ncbModelAccept;
    String AcceptBy;
    Calendar AcceptDate;
}