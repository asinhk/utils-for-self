package com.example.capitalgains.service;

import com.example.capitalgains.model.Transaction;
import com.example.capitalgains.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Cacheable(value="Transactions")
    public List<Transaction> getAllTransactions() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        log.info("{} transactions retrieved", allTransactions.size());
        log.debug(allTransactions.toString());
        return allTransactions;
    }

    public void saveTransactions(List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
        log.info("{} transactions saved successfully", transactions.size());
    }

    public boolean deleteTransactionById(Long id) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isPresent()) {
            transactionRepository.delete(transactionOptional.get());
            log.info("Successfully deleted transaction with id {}", id);
            return true;
        } else {
            log.warn("Cannot delete transaction id {} as it does not exist", id);
            return false;
        }
    }
}
