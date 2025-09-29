package com.example.NetBanking.controller;

import com.example.NetBanking.model.FundTransfer;
import com.example.NetBanking.service.FundTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/fundtransfer")
public class FundTransferController {

    @Autowired
    private FundTransferService fundTransferService;

    @PostMapping
    public FundTransfer transfer(@RequestBody Map<String, Object> request) {
        Long fromAcc = Long.valueOf(request.get("fromAcc").toString());
        Long toAcc = Long.valueOf(request.get("toAcc").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        return fundTransferService.transfer(fromAcc, toAcc, amount);
    }
}
