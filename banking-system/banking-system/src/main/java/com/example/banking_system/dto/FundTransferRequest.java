package com.example.banking_system.dto;

import java.math.BigDecimal;

public class FundTransferRequest {
    private Long fromAccountNo;
    private Long toAccountNo;
    private BigDecimal amount;

    // Getters and setters
    public Long getFromAccountNo() { return fromAccountNo; }
    public void setFromAccountNo(Long fromAccountNo) { this.fromAccountNo = fromAccountNo; }
    public Long getToAccountNo() { return toAccountNo; }
    public void setToAccountNo(Long toAccountNo) { this.toAccountNo = toAccountNo; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}