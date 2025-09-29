package com.example.NetBanking.service;

import com.example.NetBanking.model.Account;
import com.example.NetBanking.model.User;
import com.example.NetBanking.repository.AccountRepository;
import com.example.NetBanking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createAccount(Integer userId, Account account) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        account.setUser(user);
        account.setBalance(account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return accountRepository.findByUser(user);
    }

    public Account getAccountById(Long accountNo) {
        return accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account updateAccountStatus(Long accountNo, Account.Status status) {
        Account account = getAccountById(accountNo);
        account.setStatus(status);
        return accountRepository.save(account);
    }

    public void deleteAccount(Long accountNo) {
        accountRepository.deleteById(accountNo);
    }
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
