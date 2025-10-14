package com.example.banking_system.service;

import com.example.banking_system.enums.BillStatus;
import com.example.banking_system.enums.TxnStatus;
import com.example.banking_system.enums.TxnType;
import com.example.banking_system.enums.AmountType;
import com.example.banking_system.model.Account;
import com.example.banking_system.model.BillPayment;
import com.example.banking_system.model.Transaction;
import com.example.banking_system.repository.AccountRepository;
import com.example.banking_system.repository.BillPaymentRepository;
import com.example.banking_system.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class BillPaymentService {
    private final BillPaymentRepository billPaymentRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public BillPaymentService(BillPaymentRepository billPaymentRepository,
                              TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.billPaymentRepository = billPaymentRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    // ADMIN: List all bill payments, or add filter methods if needed
    public List<BillPayment> getAllBillPayments() {
        return billPaymentRepository.findAll();
    }
    public Optional<BillPayment> getBillPaymentById(Long id) {
        return billPaymentRepository.findById(id);
    }

    // USER: List their own bill payments (add repository method to filter by username/account/user_id)
    public List<BillPayment> getBillPaymentsByUsername(String username) {
        return billPaymentRepository.findByTransactionAccountUserUsername(username);
    }

    @Transactional
    public BillPayment payBill(Long accountNo, String billerName, BigDecimal amount, Date dueDate) {
        Account account = accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found."));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds.");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction txn = new Transaction();
        txn.setAccount(account);
        txn.setTxnType(TxnType.bill_payment);
        txn.setAmount(amount);
        txn.setStatus(TxnStatus.success);
        txn.setAmountType(AmountType.sent);
        txn.setCurrentBalance(account.getBalance());
        transactionRepository.save(txn);

        BillPayment bill = new BillPayment();
        bill.setTransaction(txn);
        bill.setBillerName(billerName);
        bill.setAmount(amount);
        bill.setDueDate(dueDate);
        bill.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        bill.setStatus(BillStatus.paid);
        return billPaymentRepository.save(bill);
    }
}