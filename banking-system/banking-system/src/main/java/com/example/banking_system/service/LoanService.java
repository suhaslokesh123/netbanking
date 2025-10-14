package com.example.banking_system.service;

import com.example.banking_system.dto.LoanRequest;
import com.example.banking_system.enums.LoanStatus;
import com.example.banking_system.model.Loan;
import com.example.banking_system.model.User;
import com.example.banking_system.repository.LoanRepository;
import com.example.banking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    @Autowired
    public LoanService(LoanRepository loanRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }
    // ADMIN: List all loans
    public List<Loan> getAllLoans() { return loanRepository.findAll(); }
    // ADMIN: Get loan by ID
    public Optional<Loan> getLoanById(Long id) { return loanRepository.findById(id); }
    // USER: List own loans
    public List<Loan> getLoansByUserId(Integer userId) {
        return loanRepository.findByUserUserId(userId);
    }
    // USER: Apply for loan
    public Loan applyForLoan(Loan loan, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        loan.setUser(user);
        loan.setStatus(LoanStatus.pending);
        return loanRepository.save(loan);
    }

    // USER: Apply for loan using LoanRequest
    public Loan applyForLoan(LoanRequest loanRequest, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Loan loan = new Loan();
        loan.setLoanType(loanRequest.getLoanType());
        loan.setPrincipal(loanRequest.getPrincipal());
        loan.setInterestRate(loanRequest.getInterestRate());
        loan.setDurationMonths(loanRequest.getDurationMonths());
        loan.setOutstandingAmount(loanRequest.getOutstandingAmount());
        loan.setUser(user);
        loan.setStatus(LoanStatus.pending);
        return loanRepository.save(loan);
    }
    // ADMIN: Update loan status
    public Loan updateLoanStatus(Long loanId, String status) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
        loan.setStatus(LoanStatus.valueOf(status.toLowerCase()));
        return loanRepository.save(loan);
    }

    public Loan editLoanDetails(Long loanId, Loan updatedLoan) {
        Loan existing = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        // Update only allowed fields
        if (updatedLoan.getLoanType() != null) existing.setLoanType(updatedLoan.getLoanType());
        if (updatedLoan.getPrincipal() != null) existing.setPrincipal(updatedLoan.getPrincipal());
        if (updatedLoan.getInterestRate() != null) existing.setInterestRate(updatedLoan.getInterestRate());
        if (updatedLoan.getDurationMonths() != null) existing.setDurationMonths(updatedLoan.getDurationMonths());
        if (updatedLoan.getOutstandingAmount() != null) existing.setOutstandingAmount(updatedLoan.getOutstandingAmount());
        // You can also add logic to restrict which fields can be updated, depending on your business rules.

        return loanRepository.save(existing);
    }
    // ADMIN: Delete a loan
    public void deleteLoan(Long id) { loanRepository.deleteById(id); }
}
