package com.example.NetBanking.service;

import com.example.NetBanking.model.*;
import com.example.NetBanking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FundTransferService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository txnRepo;

    @Autowired
    private FundTransferRepository transferRepo;

    @Transactional
    public FundTransfer transfer(Long fromAccNo, Long toAccNo, BigDecimal amount) {
        Account fromAcc = accountRepo.findById(fromAccNo)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAcc = accountRepo.findById(toAccNo)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAcc.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Debit
        fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
        accountRepo.save(fromAcc);

        Transaction debitTxn = new Transaction();
        debitTxn.setAccount(fromAcc);
        debitTxn.setTxnType(TransactionType.DEBIT);
        debitTxn.setAmount(amount.negate()); // debit as negative
        debitTxn.setTxnDate(LocalDateTime.now());
        debitTxn.setStatus(TransactionStatus.SUCCESS);
        txnRepo.save(debitTxn);

        // Credit
        toAcc.setBalance(toAcc.getBalance().add(amount));
        accountRepo.save(toAcc);

        Transaction creditTxn = new Transaction();
        creditTxn.setAccount(toAcc);
        creditTxn.setTxnType(TransactionType.CREDIT);
        creditTxn.setAmount(amount);
        creditTxn.setTxnDate(LocalDateTime.now());
        creditTxn.setStatus(TransactionStatus.SUCCESS);
        txnRepo.save(creditTxn);

        // FundTransfer record
        FundTransfer transfer = new FundTransfer();
        transfer.setTransaction(debitTxn); // link with debit txn
        transfer.setFromAccount(fromAcc);
        transfer.setToAccount(toAcc);
        transfer.setAmount(amount);
        transfer.setTransferDate(LocalDateTime.now());
        transfer.setStatus(TransactionStatus.SUCCESS);

        return transferRepo.save(transfer);
    }
}
