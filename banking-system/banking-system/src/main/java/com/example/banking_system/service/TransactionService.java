package com.example.banking_system.service;

import com.example.banking_system.enums.AccountStatus;
import com.example.banking_system.enums.TxnStatus;
import com.example.banking_system.enums.TxnType;
import com.example.banking_system.enums.AmountType;
import com.example.banking_system.model.Account;
import com.example.banking_system.model.FundTransfer;
import com.example.banking_system.model.Transaction;
import com.example.banking_system.repository.AccountRepository;
import com.example.banking_system.repository.FundTransferRepository;
import com.example.banking_system.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final FundTransferRepository fundTransferRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              FundTransferRepository fundTransferRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.fundTransferRepository = fundTransferRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    public List<Transaction> getTransactionsByAccountNo(Long accountNo) {
        return transactionRepository.findByAccountAccountNo(accountNo);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    @Transactional
    public FundTransfer performFundTransfer(Long fromAccountNo, Long toAccountNo, BigDecimal amount) {
        if (fromAccountNo.equals(toAccountNo)) {
            throw new IllegalArgumentException("Source and destination accounts must be different.");
        }

        // Get both accounts and validate
        Account fromAccount = accountRepository.findById(fromAccountNo)
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account toAccount = accountRepository.findById(toAccountNo)
                .orElseThrow(() -> new RuntimeException("To account not found"));

        if (!fromAccount.getStatus().equals(AccountStatus.active) ||
                !toAccount.getStatus().equals(AccountStatus.active)) {
            throw new RuntimeException("Both accounts must be ACTIVE for transfer.");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in source account.");
        }

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 1. Transaction for the sender (amountType = sent)
        Transaction senderTxn = new Transaction();
        senderTxn.setAccount(fromAccount);
        senderTxn.setTxnType(TxnType.fund_transfer);
        senderTxn.setAmount(amount);
        senderTxn.setAmountType(AmountType.sent);
        senderTxn.setCurrentBalance(fromAccount.getBalance());
        senderTxn.setStatus(TxnStatus.success);
        transactionRepository.save(senderTxn);

        // 2. Transaction for the receiver (amountType = received)
        Transaction receiverTxn = new Transaction();
        receiverTxn.setAccount(toAccount);
        receiverTxn.setTxnType(TxnType.fund_transfer);
        receiverTxn.setAmount(amount);
        receiverTxn.setAmountType(AmountType.received);
        receiverTxn.setCurrentBalance(toAccount.getBalance());
        receiverTxn.setStatus(TxnStatus.success);
        transactionRepository.save(receiverTxn);

        // Create FundTransfer referencing the senderTxn
        FundTransfer transfer = new FundTransfer();
        transfer.setTransaction(senderTxn); // Links the main transaction record to the fund transfer
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(amount);
        transfer.setStatus(TxnStatus.success);
        return fundTransferRepository.save(transfer);
    }
}