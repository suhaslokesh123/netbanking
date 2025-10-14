package com.example.banking_system.repository;
import com.example.banking_system.model.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {
    // For user fund transfer listing by username (traverse fromAccount -> User)
    List<FundTransfer> findByFromAccountUserUsername(String username);
}