package com.example.banking_system.controller.user;

import com.example.banking_system.dto.BillPaymentResponse;
import com.example.banking_system.dto.FundTransferRequest;
import com.example.banking_system.dto.FundTransferResponse;
import com.example.banking_system.dto.TransactionResponse;
import com.example.banking_system.dto.BillPaymentRequest;
import com.example.banking_system.model.BillPayment;
import com.example.banking_system.model.Transaction;
import com.example.banking_system.model.FundTransfer;
import com.example.banking_system.service.BillPaymentService;
import com.example.banking_system.service.TransactionService;
import com.example.banking_system.service.FundTransferService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/user/transactions")
public class UserTransactionController {
    private final TransactionService transactionService;
    private final BillPaymentService billPaymentService;
    private final FundTransferService fundTransferService;

    public UserTransactionController(TransactionService transactionService, BillPaymentService billPaymentService,
            FundTransferService fundTransferService) {
        this.transactionService = transactionService;
        this.billPaymentService = billPaymentService;
        this.fundTransferService = fundTransferService;
    }

    @PostMapping("/fund-transfer")
    public FundTransferResponse fundTransfer(@RequestBody FundTransferRequest request) {
        FundTransfer ft = transactionService.performFundTransfer(request.getFromAccountNo(), request.getToAccountNo(),
                request.getAmount());
        FundTransferResponse dto = new FundTransferResponse();
        dto.setTransferId(ft.getTransferId());
        dto.setTxnId(ft.getTransaction().getTxnId());
        dto.setFromAccountNo(ft.getFromAccount().getAccountNo());
        dto.setToAccountNo(ft.getToAccount().getAccountNo());
        dto.setAmount(ft.getAmount());
        dto.setTransferDate(ft.getTransferDate());
        dto.setStatus(ft.getStatus().name().toLowerCase());
        return dto;
    }

    @PostMapping("/bill-payment")
    public BillPaymentResponse payBill(@RequestBody BillPaymentRequest request) {
        java.sql.Date sqlDueDate = java.sql.Date.valueOf(request.getDueDate());
        BillPayment bill = billPaymentService.payBill(request.getAccountNo(), request.getBillerName(),
                request.getAmount(), sqlDueDate);
        BillPaymentResponse dto = new BillPaymentResponse();
        dto.setBillId(bill.getBillId());
        dto.setTxnId(bill.getTransaction().getTxnId());
        dto.setAccountNo(bill.getTransaction().getAccount().getAccountNo());
        dto.setBillerName(bill.getBillerName());
        dto.setAmount(bill.getAmount());
        dto.setDueDate(bill.getDueDate());
        dto.setPaymentDate(bill.getPaymentDate());
        dto.setStatus(bill.getStatus().name().toLowerCase());
        return dto;
    }

    @GetMapping("/fund-transfers")
    public List<FundTransferResponse> getMyFundTransfers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<FundTransfer> transfers = fundTransferService.getFundTransfersByUsername(username);
        List<FundTransferResponse> dtos = new ArrayList<>();
        for (FundTransfer ft : transfers) {
            FundTransferResponse dto = new FundTransferResponse();
            dto.setTransferId(ft.getTransferId());
            dto.setTxnId(ft.getTransaction().getTxnId());
            dto.setFromAccountNo(ft.getFromAccount().getAccountNo());
            dto.setToAccountNo(ft.getToAccount().getAccountNo());
            dto.setAmount(ft.getAmount());
            dto.setTransferDate(ft.getTransferDate());
            dto.setStatus(ft.getStatus().name().toLowerCase());
            dtos.add(dto);
        }
        return dtos;
    }

    @GetMapping("/bill-payments")
    public List<BillPaymentResponse> getMyBillPayments() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<BillPayment> bills = billPaymentService.getBillPaymentsByUsername(username);
        List<BillPaymentResponse> dtos = new ArrayList<>();
        for (BillPayment bill : bills) {
            BillPaymentResponse dto = new BillPaymentResponse();
            dto.setBillId(bill.getBillId());
            dto.setTxnId(bill.getTransaction().getTxnId());
            dto.setAccountNo(bill.getTransaction().getAccount().getAccountNo());
            dto.setBillerName(bill.getBillerName());
            dto.setAmount(bill.getAmount());
            dto.setDueDate(bill.getDueDate());
            dto.setPaymentDate(bill.getPaymentDate());
            dto.setStatus(bill.getStatus().name().toLowerCase());
            dtos.add(dto);
        }
        return dtos;
    }

    @GetMapping("/account/{accountId}")
    public List<TransactionResponse> getTransactionsByAccountId(@PathVariable Long accountId) {
        // Fetch all transactions for the account
        List<Transaction> transactions = transactionService.getTransactionsByAccountNo(accountId);

        // Map to DTOs
        List<TransactionResponse> transactionDtos = new ArrayList<>();
        for (Transaction t : transactions) {
            TransactionResponse tr = new TransactionResponse();
            tr.setTxnId(t.getTxnId());
            tr.setAccountNo(accountId);
            tr.setTxnType(t.getTxnType().toString()); 
            tr.setAmount(t.getAmount());
            tr.setTxnDate(t.getTxnDate().toString());
            tr.setStatus(t.getStatus().name().toLowerCase()); // lowercase for consistency
            transactionDtos.add(tr);
        }

        return transactionDtos;
    }

}