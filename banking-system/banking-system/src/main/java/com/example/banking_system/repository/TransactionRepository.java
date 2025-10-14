package com.example.banking_system.repository;
import com.example.banking_system.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountAccountNo(Long accountNo); // List transactions for an account
}