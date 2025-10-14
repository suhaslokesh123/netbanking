package com.example.banking_system.service;

import com.example.banking_system.model.Account;
import com.example.banking_system.model.User;
import com.example.banking_system.model.Transaction;
import com.example.banking_system.repository.AccountRepository;
import com.example.banking_system.repository.UserRepository;
import com.example.banking_system.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    // ADMIN: List all accounts
    public List<Account> getAllAccounts() { return accountRepository.findAll(); }

    // USER: List user’s own accounts
    public List<Account> getAccountsByUserId(Integer userId) {
        return accountRepository.findByUserUserId(userId);
    }

    // ADMIN/USER: Get account by number (add ownership checks at controller if needed)
    public Account getAccountByAccountNo(Long accountNo) {
        return accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    // ADMIN: Create account for user
    public Account createAccount(Account account, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        account.setUser(user);
        if (account.getBalance() == null)
            account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    // ADMIN: Update account
    public Account updateAccount(Long accountNo, Account updatedAccount) {
        Account existing = getAccountByAccountNo(accountNo);
        if (updatedAccount.getBalance() != null) {
            existing.setBalance(updatedAccount.getBalance());
        }
        if (updatedAccount.getStatus() != null) {
            existing.setStatus(updatedAccount.getStatus());
        }
        // Don't update accountNo, user, createdAt, etc.
        return accountRepository.save(existing);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    // List transactions for an account (ADMIN/USER, user ownership should be checked at controller)
    public List<Transaction> getTransactionsByAccountNo(Long accountNo) {
        return transactionRepository.findByAccountAccountNo(accountNo);
    }
}
