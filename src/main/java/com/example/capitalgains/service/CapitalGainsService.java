package com.example.capitalgains.service;

import com.example.capitalgains.exception.FifoCalculatorException;
import com.example.capitalgains.exception.InvalidInputException;
import com.example.capitalgains.calculator.FifoCalculator;
import com.example.capitalgains.model.CapitalGainResult;
import com.example.capitalgains.model.SecurityPrice;
import com.example.capitalgains.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CapitalGainsService {
    @Autowired
    private SecurityPriceService securityPriceService;
    @Autowired
    private FifoCalculator fifoCalculator;
    @Autowired
    private TransactionService transactionService;

    public CapitalGainResult getCapitalGainsForWithdrawalAmount(double withdrawalAmount) {
        CapitalGainResult capitalGainResult = CapitalGainResult.builder().withdrawalAmount(withdrawalAmount).capitalGainsPerSecurity(new ArrayList<>()).build();
        Map<String, SecurityPrice> securityPriceMap = securityPriceService.getAllSecurityPrices()
                .stream()
                .collect(Collectors.toMap(SecurityPrice::getSecurity_name, Function.identity()));
        Map<String, List<Transaction>> transactionMap = transactionService.getAllTransactions().stream().collect(Collectors.groupingBy(Transaction::getSecurity_name));
        securityPriceMap.keySet().stream().forEach(s -> {
            double capitalGain = 0;
            try {
                capitalGain = fifoCalculator.capitalGainBasedOnFifo(withdrawalAmount, securityPriceMap.get(s).getPrice(), transactionMap.get(s));
            } catch (InvalidInputException e) {
                throw new RuntimeException(e);
            } catch (FifoCalculatorException e) {
                throw new RuntimeException(e);
            }
            capitalGainResult.getCapitalGainsPerSecurity().add(CapitalGainResult.CapitalGain.builder().securityName(s).capitalGains(capitalGain).build());
        });
        return capitalGainResult;
    }
}
