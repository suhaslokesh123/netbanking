package com.example.NetBanking.model;

/**
 * @author suhal
 **/
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txnId;

    @ManyToOne
    @JoinColumn(name = "account_no", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType txnType;

    private BigDecimal amount;

    private LocalDateTime txnDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    // Getters and Setters
    public Long getTxnId() { return txnId; }
    public void setTxnId(Long txnId) { this.txnId = txnId; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public TransactionType getTxnType() { return txnType; }
    public void setTxnType(TransactionType txnType) { this.txnType = txnType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getTxnDate() { return txnDate; }
    public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}
