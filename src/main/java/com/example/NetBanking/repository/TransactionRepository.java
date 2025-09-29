package com.example.NetBanking.repository;

import com.example.NetBanking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_AccountNo(Long accountNo);
}
