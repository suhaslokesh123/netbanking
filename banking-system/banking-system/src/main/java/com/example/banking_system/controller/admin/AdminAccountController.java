package com.example.banking_system.controller.admin;

import com.example.banking_system.dto.AccountResponse;
import com.example.banking_system.dto.CardResponse;
import com.example.banking_system.dto.TransactionResponse;
import com.example.banking_system.model.Account;
import com.example.banking_system.model.Card;
import com.example.banking_system.model.Transaction;
import com.example.banking_system.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pavithrm
 **/
@RestController
@CrossOrigin(origins = "http://localhost:8000")
@RequestMapping("/admin/accounts")
public class AdminAccountController {
    private final AccountService accountService;
    public AdminAccountController(AccountService accountService) { this.accountService = accountService; }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountResponse> responseList = new ArrayList<>();
        for (Account acc : accounts) {
            AccountResponse dto = new AccountResponse();
            dto.setAccountNo(acc.getAccountNo());
            dto.setUsername(acc.getUser().getUsername());
            dto.setAccountType(acc.getAccountType() != null ? acc.getAccountType().name() : null);
            dto.setBalance(acc.getBalance());
            dto.setStatus(acc.getStatus() != null ? acc.getStatus().name() : null);
            dto.setCreatedAt(acc.getCreatedAt());
            // Do NOT set dto.setCards(...) or dto.setTransactions(...)
            responseList.add(dto);
        }
        return responseList;
    }

//    @GetMapping
//    public List<AccountResponse> getAllAccounts() {
//        List<Account> accounts = accountService.getAllAccounts();
//        List<AccountResponse> responseList = new ArrayList<>();
//        for (Account acc : accounts) {
//            AccountResponse dto = new AccountResponse();
//            dto.setAccountNo(acc.getAccountNo());
//            dto.setUsername(acc.getUser().getUsername());
//            dto.setAccountType(acc.getAccountType() != null ? acc.getAccountType().name() : null);
//            dto.setBalance(acc.getBalance());
//            dto.setStatus(acc.getStatus() != null ? acc.getStatus().name() : null);
//            dto.setCreatedAt(acc.getCreatedAt());
//
//            // Map only flat fields for cards
//            List<CardResponse> cardDtos = new ArrayList<>();
//            for (Card c : acc.getCards()) {
//                CardResponse cr = new CardResponse();
//                cr.setCardId(c.getCardId());
//                cr.setCardNumber(c.getCardNumber());
//                cr.setCardType(c.getCardType() != null ? c.getCardType().name() : null);
//                cr.setExpiryDate(c.getExpiryDate() != null ? c.getExpiryDate().toString() : null);
//                cr.setStatus(c.getStatus() != null ? c.getStatus().name() : null);
//                cardDtos.add(cr);
//            }
//            dto.setCards(cardDtos);
//
//            // Map only flat fields for transactions
//            List<TransactionResponse> txnDtos = new ArrayList<>();
//            for (Transaction t : acc.getTransactions()) {
//                TransactionResponse tr = new TransactionResponse();
//                tr.setTxnId(t.getTxnId());
//                tr.setTxnType(t.getTxnType() != null ? t.getTxnType().name() : null);
//                tr.setAmount(t.getAmount());
//                tr.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
//                tr.setAmountType(t.getAmountType() != null ? t.getAmountType().name() : null);
//                tr.setCurrentBalance(t.getCurrentBalance());
//                tr.setTxnDate(t.getTxnDate() != null ? t.getTxnDate().toString() : null);
//                txnDtos.add(tr);
//            }
//            dto.setTransactions(txnDtos);
//
//            responseList.add(dto);
//        }
//        return responseList;
//    }

    @GetMapping("/user/{userId}")
    public List<AccountResponse> getAccountsByUser(@PathVariable Integer userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
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

    @GetMapping("/{accountNo}")
    public AccountResponse getAccountByAccountNo(@PathVariable Long accountNo) {
        Account acc = accountService.getAccountByAccountNo(accountNo);
        AccountResponse dto = new AccountResponse();
        dto.setAccountNo(acc.getAccountNo());
        dto.setUsername(acc.getUser().getUsername());
        dto.setAccountType(acc.getAccountType().name());
        dto.setBalance(acc.getBalance());
        dto.setStatus(acc.getStatus().name());
        dto.setCreatedAt(acc.getCreatedAt());
        return dto;
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account, @RequestParam Integer userId) {
        return accountService.createAccount(account, userId);
    }

    @DeleteMapping("/{accountNo}")
    public void deleteAccount(@PathVariable Long accountNo) {
        accountService.deleteAccount(accountNo);
    }

    @PatchMapping("/{accountNo}")
    public Account updateAccount(@PathVariable Long accountNo, @RequestBody Account account) {
        return accountService.updateAccount(accountNo, account);
    }

    // List transactions for account
    @GetMapping("/{accountNo}/transactions")
    public List<TransactionResponse> getTransactionsForAccount(@PathVariable Long accountNo) {
        List<Transaction> txns = accountService.getTransactionsByAccountNo(accountNo);
        List<TransactionResponse> txnDtos = new ArrayList<>();
        for (Transaction t : txns) {
            TransactionResponse tr = new TransactionResponse();
            tr.setTxnId(t.getTxnId());
            tr.setTxnType(t.getTxnType() != null ? t.getTxnType().name() : null);
            tr.setAmount(t.getAmount());
            tr.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
            tr.setAmountType(t.getAmountType() != null ? t.getAmountType().name() : null);
            tr.setCurrentBalance(t.getCurrentBalance());
            tr.setTxnDate(t.getTxnDate() != null ? t.getTxnDate().toString() : null);
            txnDtos.add(tr);
        }
        return txnDtos;
    }

}
