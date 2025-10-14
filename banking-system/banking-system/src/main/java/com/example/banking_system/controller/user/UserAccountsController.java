package com.example.banking_system.controller.user;

import com.example.banking_system.dto.AccountResponse;
import com.example.banking_system.dto.TransactionResponse;
import com.example.banking_system.model.Account;
import com.example.banking_system.model.Transaction;
import com.example.banking_system.model.User;
import com.example.banking_system.repository.UserRepository;
import com.example.banking_system.service.AccountService;
import com.example.banking_system.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/user/accounts")
public class UserAccountsController {
    private final AccountService accountService;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public UserAccountsController(AccountService accountService, UserRepository userRepository, TransactionService transactionService) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<AccountResponse> getMyAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());
        List<AccountResponse> responseList = new ArrayList<>();
        for (Account acc : accounts) {
            AccountResponse dto = new AccountResponse();
            dto.setAccountNo(acc.getAccountNo());
            dto.setUsername(acc.getUser().getUsername());
            dto.setAccountType(acc.getAccountType().name());
            dto.setBalance(acc.getBalance());
            dto.setStatus(acc.getStatus().name());
            dto.setCreatedAt(acc.getCreatedAt());
            responseList.add(dto);
        }
        return responseList;
    }

    @GetMapping("/{userId}")
    public List<AccountResponse> getAccountsByUserId(@PathVariable Integer userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId.intValue());
        List<AccountResponse> responseList = new ArrayList<>();
        for (Account acc : accounts) {
            AccountResponse dto = new AccountResponse();
            dto.setAccountNo(acc.getAccountNo());
            dto.setUsername(acc.getUser().getUsername());
            dto.setAccountType(acc.getAccountType().name());
            dto.setBalance(acc.getBalance());
            dto.setStatus(acc.getStatus().name());
            dto.setCreatedAt(acc.getCreatedAt());
            responseList.add(dto);
        }
        return responseList;
    }

    // @GetMapping("/{accountNo}")
    // public AccountResponse getAccountByAccountNo(@PathVariable Long accountNo) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String username = authentication.getName();
    //     User user = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("User not found"));
    //     Account acc = accountService.getAccountByAccountNo(accountNo);
    //     if (!acc.getUser().getUserId().equals(user.getUserId()))
    //         throw new RuntimeException("You may only view your own accounts");
    //     AccountResponse dto = new AccountResponse();
    //     dto.setAccountNo(acc.getAccountNo());
    //     dto.setUsername(acc.getUser().getUsername());
    //     dto.setAccountType(acc.getAccountType().name());
    //     dto.setBalance(acc.getBalance());
    //     dto.setStatus(acc.getStatus().name());
    //     dto.setCreatedAt(acc.getCreatedAt());
    //     return dto;
    // }

    @GetMapping("/{accountNo}/transactions")
    public List<TransactionResponse> getMyAccountTransactions(@PathVariable Long accountNo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account account = accountService.getAccountByAccountNo(accountNo);
        if (!account.getUser().getUserId().equals(user.getUserId()))
            throw new RuntimeException("You may only view your own account's transactions");
        List<Transaction> txns = transactionService.getTransactionsByAccountNo(accountNo);
        List<TransactionResponse> txnDtos = new ArrayList<>();
        for (Transaction t : txns) {
            TransactionResponse tr = new TransactionResponse();
            tr.setTxnId(t.getTxnId());
            tr.setTxnType(t.getTxnType().name());
            tr.setAmount(t.getAmount());
            tr.setStatus(t.getStatus().name());
            tr.setAmountType(t.getAmountType() != null ? t.getAmountType().name() : null);
            tr.setCurrentBalance(t.getCurrentBalance());
            tr.setTxnDate(t.getTxnDate() != null ? t.getTxnDate().toString() : null);
            txnDtos.add(tr);
        }
        return txnDtos;
    }
}
