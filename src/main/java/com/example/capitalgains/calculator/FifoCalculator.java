package com.example.capitalgains.calculator;

import com.example.capitalgains.exception.FifoCalculatorException;
import com.example.capitalgains.exception.InvalidInputException;
import com.example.capitalgains.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class FifoCalculator {

    public double capitalGainBasedOnFifo(Double withdrawalAmount, Double sellingPrice, List<Transaction> transactions) throws InvalidInputException, FifoCalculatorException {
        if (null == transactions || transactions.size() < 1 || withdrawalAmount < 0 || sellingPrice < 0) {
            log.error("Invalid inputs sent to FifoCalculator. Withdrawal amount: {}, sellingPrice: {}, transactions: {}", withdrawalAmount, sellingPrice, transactions);
            throw new InvalidInputException("Invalid inputs sent to calculator. Cannot calculate capital gains.");
        }

        //Check if sufficient units for withdrawal to happen
        String securityName = transactions.stream().findAny().get().getSecurity_name();
        log.info("Starting capital gains calculations for {}", securityName);
        Double avlblUnits = transactions.stream().map(Transaction::getUnits).reduce(0d, (total, units) -> total = total + units);
        double amountAvlbl = avlblUnits * sellingPrice;
        if (amountAvlbl < withdrawalAmount) {
            log.error("Insufficient units. Current value: {}, Withdrawal amount: {}", amountAvlbl, withdrawalAmount);
            throw new InvalidInputException("Available units cannot cover requested withdrawal amount for :" + securityName);
        }

        Deque<Transaction> buyTransactions = new ArrayDeque<>();
        try {
            Double soldUnits = Math.abs(transactions
                    .stream()
                    .filter(t -> t.getUnits() < 0)
                    .map(Transaction::getUnits)
                    .reduce(0d, (sum, u) -> sum += u));

            transactions
                    .stream()
                    .filter(t -> t.getUnits() > 0)
                    .sorted(Comparator.comparing(Transaction::getTransaction_dt, (d1, d2) -> d1.isBefore(d2) ? -1 : 1))
                    .forEach(t -> buyTransactions.addLast(t));

            //Adjust all the sold units from buy units based on fifo
            while (soldUnits > 0) {
                double buyAdjustedSoldUnits = soldUnits - buyTransactions.peekFirst().getUnits();
                if (buyTransactions.peekFirst().getUnits() <= soldUnits) {
                    buyTransactions.removeFirst(); //remove the oldest buy from the queue
                } else {
                    double newBuyUnits = buyTransactions.peekFirst().getUnits() - soldUnits;
                    buyTransactions.peekFirst().setUnits(newBuyUnits);
                }
                soldUnits = buyAdjustedSoldUnits;
            }

            double unitsToWithdraw = withdrawalAmount / sellingPrice;
            log.info("Units to withdraw: {}", unitsToWithdraw);
            double totalCostOfAquisition = 0;
            while (unitsToWithdraw > 0) {
                double unitsToWithdrawRemaining = unitsToWithdraw - buyTransactions.peekFirst().getUnits();
                if (buyTransactions.peekFirst().getUnits() <= unitsToWithdraw) {
                    totalCostOfAquisition += buyTransactions.peekFirst().getUnits() * buyTransactions.peekFirst().getPrice();
                    buyTransactions.removeFirst(); //remove the oldest buy from the queue
                } else {
                    totalCostOfAquisition += unitsToWithdraw * buyTransactions.peekFirst().getPrice();
                }
                unitsToWithdraw = unitsToWithdrawRemaining;
            }

            double capitalGain = withdrawalAmount - totalCostOfAquisition;
            log.info("Security: {}, Sale proceeds: {}, cost of acquisition: {}, capital gains: {}", securityName, withdrawalAmount, totalCostOfAquisition, capitalGain);
            return capitalGain;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new FifoCalculatorException("Error calculating capital gains for security: " + securityName);
        }
    }

}
