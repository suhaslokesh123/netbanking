package com.example.banking_system.repository;
import com.example.banking_system.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    // List all cards belonging to a user's accounts
    List<Card> findByAccountUserUsername(String username);

    // List all cards belonging to a specific account
    List<Card> findByAccountAccountNo(Long accountNo);
}
