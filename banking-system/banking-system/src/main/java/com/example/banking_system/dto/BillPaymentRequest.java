package com.example.banking_system.dto;

import java.math.BigDecimal;

public class BillPaymentRequest {
    private Long accountNo;
    private String billerName;
    private BigDecimal amount;
    private String dueDate; // Use String for simple parsing, or java.util.Date with @DateTimeFormat if desired

    // Getters and setters
    public Long getAccountNo() { return accountNo; }
    public void setAccountNo(Long accountNo) { this.accountNo = accountNo; }
    public String getBillerName() { return billerName; }
    public void setBillerName(String billerName) { this.billerName = billerName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}