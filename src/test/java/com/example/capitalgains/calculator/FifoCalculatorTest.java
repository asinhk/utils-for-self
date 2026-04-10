package com.example.capitalgains.calculator;

import com.example.capitalgains.exception.FifoCalculatorException;
import com.example.capitalgains.exception.InvalidInputException;
import com.example.capitalgains.model.BuySell;
import com.example.capitalgains.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FifoCalculatorTest {

    private FifoCalculator fifoCalculator;

    @BeforeEach
    void setUp() {
        fifoCalculator = new FifoCalculator();
    }

    @Test
    void capitalGainBasedOnFifo_insufficientUnits_throwsInvalidInputException() {
        Double withdrawalAmount = 1000.0;
        Double sellingPrice = 100.0;
        List<Transaction> transactions = List.of(
            Transaction.builder()
                .security_name("Test Security")
                .units(5.0)
                .price(50.0)
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .build()
        );

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
            fifoCalculator.capitalGainBasedOnFifo(withdrawalAmount, sellingPrice, transactions));

        assertEquals("Available units cannot cover requested withdrawal amount for :Test Security", exception.getErrorMsg());
    }

    @Test
    void capitalGainBasedOnFifo_normalCalculation_returnsCorrectGain() throws InvalidInputException, FifoCalculatorException {
        Double withdrawalAmount = 200.0; // 2 units at 100 price
        Double sellingPrice = 100.0;
        List<Transaction> transactions = List.of(
            // Buy 3 units at 50
            Transaction.builder()
                .security_name("Test Security")
                .units(3.0)
                .price(50.0)
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .build(),
            // Sell 1 unit (negative)
            Transaction.builder()
                .security_name("Test Security")
                .units(-1.0)
                .price(60.0)
                .transaction_dt(LocalDate.of(2023, 2, 1))
                .buy_sell(BuySell.S)
                .build()
        );

        // Available units: 3 - 1 = 2
        // Cost of acquisition: 2 units from first buy at 50 = 100
        // Sale proceeds: 200
        // Gain: 200 - 100 = 100

        double result = fifoCalculator.capitalGainBasedOnFifo(withdrawalAmount, sellingPrice, transactions);

        assertEquals(100.0, result, 0.01);
    }

    @Test
    void capitalGainBasedOnFifo_exactUnits_returnsCorrectGain() throws InvalidInputException, FifoCalculatorException {
        Double withdrawalAmount = 300.0; // 3 units at 100
        Double sellingPrice = 100.0;
        List<Transaction> transactions = List.of(
            Transaction.builder()
                .security_name("Test Security")
                .units(3.0)
                .price(50.0)
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .build()
        );

        // Cost: 3 * 50 = 150
        // Gain: 300 - 150 = 150

        double result = fifoCalculator.capitalGainBasedOnFifo(withdrawalAmount, sellingPrice, transactions);

        assertEquals(150.0, result, 0.01);
    }

    @Test
    void capitalGainBasedOnFifo_multipleBuys_returnsCorrectGain() throws InvalidInputException, FifoCalculatorException {
        Double withdrawalAmount = 250.0; // 2.5 units at 100
        Double sellingPrice = 100.0;
        List<Transaction> transactions = List.of(
            // Buy 2 at 40
            Transaction.builder()
                .security_name("Test Security")
                .units(2.0)
                .price(40.0)
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .build(),
            // Buy 2 at 60
            Transaction.builder()
                .security_name("Test Security")
                .units(2.0)
                .price(60.0)
                .transaction_dt(LocalDate.of(2023, 2, 1))
                .buy_sell(BuySell.B)
                .build(),
            // Sell 1.5 (negative)
            Transaction.builder()
                .security_name("Test Security")
                .units(-1.5)
                .price(80.0)
                .transaction_dt(LocalDate.of(2023, 3, 1))
                .buy_sell(BuySell.S)
                .build()
        );

        // Sold 1.5, so remaining: first buy 2 - 1.5 = 0.5, second buy 2
        // Withdraw 2.5: 0.5 at 40 + 2 at 60 = 20 + 120 = 140
        // Gain: 250 - 140 = 110

        double result = fifoCalculator.capitalGainBasedOnFifo(withdrawalAmount, sellingPrice, transactions);

        assertEquals(110.0, result, 0.01);
    }

    @Test
    void capitalGainBasedOnFifo_emptyTransactions_throwsInvalidInputException() {
        Double withdrawalAmount = 100.0;
        Double sellingPrice = 100.0;
        List<Transaction> transactions = List.of();

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
            fifoCalculator.capitalGainBasedOnFifo(withdrawalAmount, sellingPrice, transactions));

        assertEquals("Invalid inputs sent to calculator. Cannot calculate capital gains.", exception.getErrorMsg());
    }

    @Test
    void capitalGainBasedOnFifo_partialWithdrawal_returnsCorrectGain() throws InvalidInputException, FifoCalculatorException {
        Double withdrawalAmount = 150.0; // 1.5 units at 100
        Double sellingPrice = 100.0;
        List<Transaction> transactions = List.of(
            Transaction.builder()
                .security_name("Test Security")
                .units(3.0)
                .price(50.0)
                .transaction_dt(LocalDate.of(2023, 1, 1))
                .buy_sell(BuySell.B)
                .build()
        );

        // Withdraw 1.5 from 3, cost: 1.5 * 50 = 75
        // Gain: 150 - 75 = 75

        double result = fifoCalculator.capitalGainBasedOnFifo(withdrawalAmount, sellingPrice, transactions);

        assertEquals(75.0, result, 0.01);
    }
}
