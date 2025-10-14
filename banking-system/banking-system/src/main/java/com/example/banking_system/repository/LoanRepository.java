package com.example.banking_system.repository;
import com.example.banking_system.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserUserId(Integer userId); // List loans by user
}