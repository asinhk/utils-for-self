package com.example.capitalgains.controller;


import com.example.capitalgains.model.*;
import com.example.capitalgains.service.*;
import com.example.capitalgains.util.ModelMapperHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/capitalgainsservice")
public class CapitalGainsServiceController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private TransactionUploadService transactionUploadService;
    @Autowired
    private SecurityPriceService securityPriceService;
    @Autowired
    private CapitalGainsService capitalGainsService;

    @GetMapping("/getAllTransactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping("/saveTransaction")
    public void saveTransaction(CreateTransactionDTO createTransactionDTO) {
        Transaction transaction = ModelMapperHolder.getModelMapper().map(createTransactionDTO, Transaction.class);
        transactionService.saveTransactions(List.of(transaction));
    }

    @DeleteMapping("/deleteTransaction/{id}")
    public boolean deleteTransactionById(@PathVariable long id) {
        return transactionService.deleteTransactionById(id);
    }

    @GetMapping("/getAllSecurities")
    public List<Security> getAllSecurities() {
        return securityService.getAllSecurities();
    }

    @PostMapping("/saveSecurity")
    public void saveSecurity(CreateSecurityDTO createSecurityDTO) {
        Security security = ModelMapperHolder.getModelMapper().map(createSecurityDTO, Security.class);
        securityService.saveSecurities(List.of(security));
    }

    @DeleteMapping("/deleteSecurity/{id}")
    public boolean deleteSecurityById(@PathVariable long id) {
        return securityService.deleteSecurityById(id);
    }


    @PostMapping(value = "/upload-transactions-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadTransactionsFromFile(@RequestParam("file") MultipartFile file) {
        transactionUploadService.uploadTransactionsFromFile(file);
    }

    @GetMapping("/getAllPrices")
    public List<SecurityPrice> getAllSecurityPricess() {
        return securityPriceService.getAllSecurityPrices();
    }

    @PostMapping("/saveAllPrices")
    public void saveAllSecurityPrices(@RequestBody List<CreateSecurityPriceDTO> createSecurityPriceDTOList) {
        securityPriceService.saveAllSecurityPrices(createSecurityPriceDTOList);
    }

    @GetMapping("/getCapitalGainsForAmount")
    public CapitalGainResult getCapitalGainsForamount(@RequestParam Double withdrawalAmount) {
        return capitalGainsService.getCapitalGainsForWithdrawalAmount(withdrawalAmount);
    }

}
