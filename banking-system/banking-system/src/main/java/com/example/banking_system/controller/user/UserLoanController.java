package com.example.banking_system.controller.user;

import com.example.banking_system.dto.LoanRequest;
import com.example.banking_system.dto.LoanResponse;
import com.example.banking_system.model.Loan;
import com.example.banking_system.model.User;
import com.example.banking_system.repository.UserRepository;
import com.example.banking_system.service.LoanService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/user/loans")
public class UserLoanController {
    private final LoanService loanService;
    private final UserRepository userRepository;

    public UserLoanController(LoanService loanService, UserRepository userRepository) {
        this.loanService = loanService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<LoanResponse> getMyLoans() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Loan> loans = loanService.getLoansByUserId(user.getUserId());
        List<LoanResponse> dtos = new ArrayList<>();
        for (Loan saved : loans) {
            LoanResponse dto = new LoanResponse();
            dto.setLoanId(saved.getLoanId());
            dto.setUserId(saved.getUser().getUserId());
            dto.setLoanType(saved.getLoanType().name());
            dto.setPrincipal(saved.getPrincipal());
            dto.setInterestRate(saved.getInterestRate());
            dto.setDurationMonths(saved.getDurationMonths());
            dto.setOutstandingAmount(saved.getOutstandingAmount());
            dto.setStatus(saved.getStatus().name());
            dto.setCreatedAt(saved.getCreatedAt());
            dtos.add(dto);
        }
        return dtos;
    }

    @GetMapping("/{userId}")
    public List<LoanResponse> getLoansByUserId(@PathVariable Integer userId) {
        List<Loan> loans = loanService.getLoansByUserId(userId);
        List<LoanResponse> dtos = new ArrayList<>();
        for (Loan saved : loans) {
            LoanResponse dto = new LoanResponse();
            dto.setLoanId(saved.getLoanId());
            dto.setUserId(saved.getUser().getUserId());
            dto.setLoanType(saved.getLoanType().name());
            dto.setPrincipal(saved.getPrincipal());
            dto.setInterestRate(saved.getInterestRate());
            dto.setDurationMonths(saved.getDurationMonths());
            dto.setOutstandingAmount(saved.getOutstandingAmount());
            dto.setStatus(saved.getStatus().name());
            dto.setCreatedAt(saved.getCreatedAt());
            dtos.add(dto);
        }
        return dtos;
    }

    @PostMapping("/{userId}")
public LoanResponse applyLoan(@PathVariable Integer userId, @RequestBody LoanRequest loanRequest) {
    Loan saved = loanService.applyForLoan(loanRequest, userId);

    LoanResponse dto = new LoanResponse();
    dto.setLoanId(saved.getLoanId());
    dto.setUserId(saved.getUser().getUserId());
    dto.setLoanType(saved.getLoanType().name());
    dto.setPrincipal(saved.getPrincipal());
    dto.setInterestRate(saved.getInterestRate());
    dto.setDurationMonths(saved.getDurationMonths());
    dto.setOutstandingAmount(saved.getOutstandingAmount());
    dto.setStatus(saved.getStatus().name());
    dto.setCreatedAt(saved.getCreatedAt());

    return dto;
    }

}
