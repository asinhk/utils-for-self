package com.example.capitalgains.service;

import com.example.capitalgains.calculator.FifoCalculator;
import com.example.capitalgains.exception.FifoCalculatorException;
import com.example.capitalgains.exception.InvalidInputException;
import com.example.capitalgains.model.CapitalGainResult;
import com.example.capitalgains.model.SecurityPrice;
import com.example.capitalgains.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapitalGainsServiceTest {

    @Mock
    private SecurityPriceService securityPriceService;

    @Mock
    private FifoCalculator fifoCalculator;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CapitalGainsService capitalGainsService;


    @Test
    void getCapitalGainsForWithdrawalAmount_shouldReturnCorrectResult() throws InvalidInputException, FifoCalculatorException {
        double withdrawalAmount = 1000.0;

        List<SecurityPrice> securityPrices = List.of(
            SecurityPrice.builder()
                .security_name("Security1")
                .price(100.0)
                .build(),
            SecurityPrice.builder()
                .security_name("Security2")
                .price(200.0)
                .build()
        );

        List<Transaction> transactions = List.of(
            Transaction.builder()
                .security_name("Security1")
                .units(10.0)
                .price(50.0)
                .transaction_dt(LocalDate.now())
                .build(),
            Transaction.builder()
                .security_name("Security2")
                .units(5.0)
                .price(100.0)
                .transaction_dt(LocalDate.now())
                .build()
        );

        when(securityPriceService.getAllSecurityPrices()).thenReturn(securityPrices);
        when(transactionService.getAllTransactions()).thenReturn(transactions);
        when(fifoCalculator.capitalGainBasedOnFifo(eq(withdrawalAmount), eq(100.0), anyList())).thenReturn(500.0);
        when(fifoCalculator.capitalGainBasedOnFifo(eq(withdrawalAmount), eq(200.0), anyList())).thenReturn(800.0);

        CapitalGainResult result = capitalGainsService.getCapitalGainsForWithdrawalAmount(withdrawalAmount);

        assertEquals(withdrawalAmount, result.getWithdrawalAmount());
        assertEquals(2, result.getCapitalGainsPerSecurity().size());
        assertTrue(result.getCapitalGainsPerSecurity().stream().anyMatch(cg -> cg.getSecurityName().equals("Security1") && cg.getCapitalGains() == 500.0));
        assertTrue(result.getCapitalGainsPerSecurity().stream().anyMatch(cg -> cg.getSecurityName().equals("Security2") && cg.getCapitalGains() == 800.0));

        verify(securityPriceService).getAllSecurityPrices();
        verify(transactionService).getAllTransactions();
        verify(fifoCalculator, times(2)).capitalGainBasedOnFifo(anyDouble(), anyDouble(), anyList());
    }

    @Test
    void getCapitalGainsForWithdrawalAmount_invalidInputException_throwsRuntimeException() throws InvalidInputException, FifoCalculatorException {
        double withdrawalAmount = 1000.0;

        List<SecurityPrice> securityPrices = List.of(
            SecurityPrice.builder()
                .security_name("Security1")
                .price(100.0)
                .build()
        );

        List<Transaction> transactions = List.of(
            Transaction.builder()
                .security_name("Security1")
                .units(5.0)
                .price(50.0)
                .transaction_dt(LocalDate.now())
                .build()
        );

        when(securityPriceService.getAllSecurityPrices()).thenReturn(securityPrices);
        when(transactionService.getAllTransactions()).thenReturn(transactions);
        when(fifoCalculator.capitalGainBasedOnFifo(anyDouble(), anyDouble(), anyList())).thenThrow(new InvalidInputException("Insufficient units"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            capitalGainsService.getCapitalGainsForWithdrawalAmount(withdrawalAmount));

        assertInstanceOf(InvalidInputException.class, exception.getCause());
    }

    @Test
    void getCapitalGainsForWithdrawalAmount_fifoCalculatorException_throwsRuntimeException() throws InvalidInputException, FifoCalculatorException {
        double withdrawalAmount = 1000.0;

        List<SecurityPrice> securityPrices = List.of(
            SecurityPrice.builder()
                .security_name("Security1")
                .price(100.0)
                .build()
        );

        List<Transaction> transactions = List.of(
            Transaction.builder()
                .security_name("Security1")
                .units(5.0)
                .price(50.0)
                .transaction_dt(LocalDate.now())
                .build()
        );

        when(securityPriceService.getAllSecurityPrices()).thenReturn(securityPrices);
        when(transactionService.getAllTransactions()).thenReturn(transactions);
        when(fifoCalculator.capitalGainBasedOnFifo(anyDouble(), anyDouble(), anyList())).thenThrow(new FifoCalculatorException("Calculation error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            capitalGainsService.getCapitalGainsForWithdrawalAmount(withdrawalAmount));

        assertInstanceOf(FifoCalculatorException.class, exception.getCause());
    }

    @Test
    void getCapitalGainsForWithdrawalAmount_emptySecurities_returnsEmptyResult() throws InvalidInputException, FifoCalculatorException {
        double withdrawalAmount = 1000.0;

        when(securityPriceService.getAllSecurityPrices()).thenReturn(List.of());
        when(transactionService.getAllTransactions()).thenReturn(List.of());

        CapitalGainResult result = capitalGainsService.getCapitalGainsForWithdrawalAmount(withdrawalAmount);

        assertEquals(withdrawalAmount, result.getWithdrawalAmount());
        assertTrue(result.getCapitalGainsPerSecurity().isEmpty());

        verify(securityPriceService).getAllSecurityPrices();
        verify(transactionService).getAllTransactions();
        verify(fifoCalculator, never()).capitalGainBasedOnFifo(anyDouble(), anyDouble(), anyList());
    }
}
