package com.example.NetBanking.repository;

import com.example.NetBanking.model.Account;
import com.example.NetBanking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
