package com.example.NetBanking.controller;

import com.example.NetBanking.model.Account;
import com.example.NetBanking.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create a new account for a user
    @PostMapping("/user/{userId}")
    public Account createAccount(@PathVariable Integer userId, @RequestBody Account account) {
        return accountService.createAccount(userId, account);
    }

    // Get all accounts of a user
    @GetMapping("/user/{userId}")
    public List<Account> getAccountsByUser(@PathVariable Integer userId) {
        return accountService.getAccountsByUser(userId);
    }

    // Get account details by account number
    @GetMapping("/{accountNo}")
    public Account getAccount(@PathVariable Long accountNo) {
        return accountService.getAccountById(accountNo);
    }

    // Update account status (active, inactive, closed)
    @PutMapping("/{accountNo}/status")
    public Account updateStatus(@PathVariable Long accountNo, @RequestParam Account.Status status) {
        return accountService.updateAccountStatus(accountNo, status);
    }

    // Delete account
    @DeleteMapping("/{accountNo}")
    public String deleteAccount(@PathVariable Long accountNo) {
        accountService.deleteAccount(accountNo);
        return "Account " + accountNo + " deleted successfully!";
    }
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }
}
