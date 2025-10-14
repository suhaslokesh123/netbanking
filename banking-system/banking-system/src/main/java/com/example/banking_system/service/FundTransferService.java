package com.example.banking_system.service;

import com.example.banking_system.model.FundTransfer;
import com.example.banking_system.repository.FundTransferRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FundTransferService {
    private final FundTransferRepository fundTransferRepository;
    public FundTransferService(FundTransferRepository fundTransferRepository) {
        this.fundTransferRepository = fundTransferRepository;
    }

    // ADMIN: List all fund transfers
    public List<FundTransfer> getAllFundTransfers() {
        return fundTransferRepository.findAll();
    }

    public Optional<FundTransfer> getFundTransferById(Long id) {
        return fundTransferRepository.findById(id);
    }

    // USER: List fund transfers by username (from user’s accounts)
    public List<FundTransfer> getFundTransfersByUsername(String username) {
        return fundTransferRepository.findByFromAccountUserUsername(username);
    }
}