package com.example.banking_system.repository;
import com.example.banking_system.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserUserId(Integer userId);         // For listing a user's accounts
}