package com.example.banking_system.dto;

import com.example.banking_system.enums.LoanType;
import java.math.BigDecimal;

public class LoanRequest {
    private LoanType loanType;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private Integer durationMonths;
    private BigDecimal outstandingAmount;

    // Getters and setters
    public LoanType getLoanType() { return loanType; }
    public void setLoanType(LoanType loanType) { this.loanType = loanType; }

    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) { this.principal = principal; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }
}
