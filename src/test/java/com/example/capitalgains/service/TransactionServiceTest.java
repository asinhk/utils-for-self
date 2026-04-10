package com.example.capitalgains.service;

import com.example.capitalgains.model.BuySell;
import com.example.capitalgains.model.Transaction;
import com.example.capitalgains.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getAllTransactions_shouldReturnAllTransactions() {
        List<Transaction> expectedTransactions = List.of(
            Transaction.builder()
                .id(1L)
                .security_id(1L)
                .security_name("Security1")
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .price(100.0)
                .units(10.0)
                .active(1)
                .build(),
            Transaction.builder()
                .id(2L)
                .security_id(1L)
                .security_name("Security1")
                .transaction_dt(LocalDate.of(2023, 2, 1))
                .buy_sell(BuySell.S)
                .price(150.0)
                .units(-5.0)
                .active(1)
                .build()
        );

        when(transactionRepository.findAll()).thenReturn(expectedTransactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(expectedTransactions, result);
        assertEquals(2, result.size());
        verify(transactionRepository).findAll();
    }

    @Test
    void getAllTransactions_emptyList_shouldReturnEmptyList() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        List<Transaction> result = transactionService.getAllTransactions();

        assertTrue(result.isEmpty());
        verify(transactionRepository).findAll();
    }

    @Test
    void saveTransactions_shouldSaveAllTransactions() {
        List<Transaction> transactions = List.of(
            Transaction.builder()
                .security_id(1L)
                .security_name("Security1")
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .price(100.0)
                .units(10.0)
                .active(1)
                .build(),
            Transaction.builder()
                .security_id(2L)
                .security_name("Security2")
                .transaction_dt(LocalDate.of(2023, 2, 1))
                .buy_sell(BuySell.S)
                .price(200.0)
                .units(-5.0)
                .active(1)
                .build()
        );

        transactionService.saveTransactions(transactions);

        verify(transactionRepository).saveAll(transactions);
    }

    @Test
    void saveTransactions_emptyList_shouldCallRepository() {
        List<Transaction> transactions = List.of();

        transactionService.saveTransactions(transactions);

        verify(transactionRepository).saveAll(anyList());
    }

    @Test
    void saveTransactions_singleTransaction_shouldSave() {
        List<Transaction> transactions = List.of(
            Transaction.builder()
                .security_id(1L)
                .security_name("Security1")
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .price(100.0)
                .units(10.0)
                .active(1)
                .build()
        );

        transactionService.saveTransactions(transactions);

        verify(transactionRepository).saveAll(transactions);
        assertEquals(1, transactions.size());
    }

    @Test
    void deleteTransactionById_existingId_shouldReturnTrue() {
        Long id = 1L;
        Transaction transaction = Transaction.builder()
            .id(id)
            .security_id(1L)
            .security_name("Security1")
            .transaction_dt(LocalDate.of(2023, 1, 1))
            .buy_sell(BuySell.B)
            .price(100.0)
            .units(10.0)
            .active(1)
            .build();

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        boolean result = transactionService.deleteTransactionById(id);

        assertTrue(result);
        verify(transactionRepository).findById(id);
        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransactionById_nonExistentId_shouldReturnFalse() {
        Long id = 999L;

        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = transactionService.deleteTransactionById(id);

        assertFalse(result);
        verify(transactionRepository).findById(id);
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void deleteTransactionById_buyTransaction_shouldDelete() {
        Long id = 1L;
        Transaction buyTransaction = Transaction.builder()
            .id(id)
            .security_id(1L)
            .security_name("Security1")
            .transaction_dt(LocalDate.of(2023, 1, 1))
            .buy_sell(BuySell.B)
            .price(100.0)
            .units(10.0)
            .active(1)
            .build();

        when(transactionRepository.findById(id)).thenReturn(Optional.of(buyTransaction));

        boolean result = transactionService.deleteTransactionById(id);

        assertTrue(result);
        verify(transactionRepository).delete(buyTransaction);
    }

    @Test
    void deleteTransactionById_sellTransaction_shouldDelete() {
        Long id = 2L;
        Transaction sellTransaction = Transaction.builder()
            .id(id)
            .security_id(1L)
            .security_name("Security1")
            .transaction_dt(LocalDate.of(2023, 2, 1))
            .buy_sell(BuySell.S)
            .price(150.0)
            .units(-5.0)
            .active(1)
            .build();

        when(transactionRepository.findById(id)).thenReturn(Optional.of(sellTransaction));

        boolean result = transactionService.deleteTransactionById(id);

        assertTrue(result);
        verify(transactionRepository).delete(sellTransaction);
    }

    @Test
    void getAllTransactions_multipleTransactions_shouldPreserveOrder() {
        List<Transaction> expectedTransactions = List.of(
            Transaction.builder()
                .id(1L)
                .security_id(1L)
                .security_name("Security1")
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .price(100.0)
                .units(10.0)
                .active(1)
                .build(),
            Transaction.builder()
                .id(2L)
                .security_id(1L)
                .security_name("Security1")
                .transaction_dt(LocalDate.of(2023, 2, 1))
                .buy_sell(BuySell.S)
                .price(150.0)
                .units(-5.0)
                .active(1)
                .build(),
            Transaction.builder()
                .id(3L)
                .security_id(2L)
                .security_name("Security2")
                .transaction_dt(LocalDate.of(2023, 3, 1))
                .buy_sell(BuySell.B)
                .price(200.0)
                .units(20.0)
                .active(1)
                .build()
        );

        when(transactionRepository.findAll()).thenReturn(expectedTransactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());
    }
}
