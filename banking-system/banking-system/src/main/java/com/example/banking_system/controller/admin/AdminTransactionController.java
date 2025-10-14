package com.example.banking_system.controller.admin;

import com.example.banking_system.dto.TransactionResponse;
import com.example.banking_system.model.Transaction;
import com.example.banking_system.service.TransactionService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pavithrm
 **/
@RestController
@CrossOrigin(origins = "http://localhost:8000")
@RequestMapping("/admin/transactions")
public class AdminTransactionController {
    private final TransactionService transactionService;
    public AdminTransactionController(TransactionService transactionService) { this.transactionService = transactionService; }

    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionResponse> responseList = new ArrayList<>();
        for (Transaction t : transactions) {
            TransactionResponse tr = new TransactionResponse();
            tr.setTxnId(t.getTxnId());
            tr.setAccountNo(t.getAccount().getAccountNo());
            tr.setTxnType(t.getTxnType() != null ? t.getTxnType().name() : null);
            tr.setAmount(t.getAmount());
            tr.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
            tr.setAmountType(t.getAmountType() != null ? t.getAmountType().name() : null);
            tr.setCurrentBalance(t.getCurrentBalance());
            tr.setTxnDate(t.getTxnDate() != null ? t.getTxnDate().toString() : null);
            responseList.add(tr);
        }
        return responseList;
    }
}
