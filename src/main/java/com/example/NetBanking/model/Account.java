package com.example.NetBanking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountNo;   // primary key

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // foreign key to User
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType account_type;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    @Column(nullable = false, updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created_at;

    public enum AccountType {
        savings, current, fixed, loan
    }

    public enum Status {
        active, inactive, closed
    }
}
