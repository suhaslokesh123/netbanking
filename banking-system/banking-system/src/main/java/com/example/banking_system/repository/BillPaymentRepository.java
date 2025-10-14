package com.example.banking_system.repository;
import com.example.banking_system.model.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
    // For user bill payment listing by username (traverse transaction -> account -> user)
    List<BillPayment> findByTransactionAccountUserUsername(String username);
}