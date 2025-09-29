package com.example.NetBanking.service;

import com.example.NetBanking.model.Transaction;
import com.example.NetBanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository txnRepo;

    public List<Transaction> getAllTransactions() {
        return txnRepo.findAll();
    }

    public List<Transaction> getTransactionsByAccount(Long accountNo) {
        return txnRepo.findByAccount_AccountNo(accountNo);
    }
}
