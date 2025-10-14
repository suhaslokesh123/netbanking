package com.example.banking_system.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class FundTransferResponse {
    private Long transferId;
    private Long txnId;
    private Long fromAccountNo;
    private Long toAccountNo;
    private BigDecimal amount;
    private Timestamp transferDate;
    private String status;

    // Getters and setters
    public Long getTransferId() { return transferId; }
    public void setTransferId(Long transferId) { this.transferId = transferId; }

    public Long getTxnId() { return txnId; }
    public void setTxnId(Long txnId) { this.txnId = txnId; }

    public Long getFromAccountNo() { return fromAccountNo; }
    public void setFromAccountNo(Long fromAccountNo) { this.fromAccountNo = fromAccountNo; }

    public Long getToAccountNo() { return toAccountNo; }
    public void setToAccountNo(Long toAccountNo) { this.toAccountNo = toAccountNo; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Timestamp getTransferDate() { return transferDate; }
    public void setTransferDate(Timestamp transferDate) { this.transferDate = transferDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}