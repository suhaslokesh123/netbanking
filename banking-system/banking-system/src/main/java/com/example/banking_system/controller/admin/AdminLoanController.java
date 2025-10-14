package com.example.banking_system.controller.admin;

import com.example.banking_system.dto.LoanResponse;
import com.example.banking_system.model.Loan;
import com.example.banking_system.service.LoanService;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/loans")
public class AdminLoanController {
    private final LoanService loanService;
    public AdminLoanController(LoanService loanService) { this.loanService = loanService; }

    @GetMapping
    public List<LoanResponse> getAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        List<LoanResponse> responseList = new ArrayList<>();
        for (Loan loan : loans) {
            responseList.add(mapToDto(loan));
        }
        return responseList;
    }

    @GetMapping("/{loanId}")
    public LoanResponse getLoanById(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        return mapToDto(loan);
    }

    @PatchMapping("/{loanId}/status")
    public LoanResponse updateLoanStatus(@PathVariable Long loanId, @RequestParam String status) {
        Loan loan = loanService.updateLoanStatus(loanId, status);
        return mapToDto(loan);
    }

    @DeleteMapping("/{loanId}")
    public void deleteLoan(@PathVariable Long loanId) { loanService.deleteLoan(loanId); }

    @PatchMapping("/{loanId}")
    public Loan updateLoan(@PathVariable Long loanId, @RequestBody Loan updatedLoan) {
        return loanService.editLoanDetails(loanId, updatedLoan);
    }

    // Utility for mapping Loan to LoanResponse
    private LoanResponse mapToDto(Loan loan) {
        LoanResponse dto = new LoanResponse();
        dto.setLoanId(loan.getLoanId());
        dto.setUserId(loan.getUser().getUserId());
        dto.setLoanType(loan.getLoanType().name());
        dto.setPrincipal(loan.getPrincipal());
        dto.setInterestRate(loan.getInterestRate());
        dto.setDurationMonths(loan.getDurationMonths());
        dto.setOutstandingAmount(loan.getOutstandingAmount());
        dto.setStatus(loan.getStatus().name());
        dto.setCreatedAt(loan.getCreatedAt());
        return dto;
    }
}