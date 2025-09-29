package com.example.NetBanking.controller;

import com.example.NetBanking.model.Transaction;
import com.example.NetBanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Get all transactions
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    // Get transactions for a specific account
    @GetMapping("/{accountNo}")
    public List<Transaction> getTransactionsByAccount(@PathVariable Long accountNo) {
        return transactionService.getTransactionsByAccount(accountNo);
    }
}
