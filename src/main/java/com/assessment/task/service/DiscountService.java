package com.assessment.task.service;

import com.assessment.task.dto.Bill;
import com.assessment.task.dto.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class DiscountService {
    private final CurrencyExchangeService currencyExchangeService;

    @Autowired
    public DiscountService(CurrencyExchangeService currencyExchangeService) {
        this.currencyExchangeService = currencyExchangeService;
    }

    public double calculateFinalAmount(UserType user, Bill bill) {
        double totalAmount = bill.getTotalAmount();
        double groceryAmount = bill.getGroceryAmount();
        double nonGroceryAmount = totalAmount - groceryAmount;

        // Determine the highest applicable percentage discount
        double percentageDiscount = getApplicableDiscount(user);

        // Apply percentage discount only on non-grocery items
        double discountedNonGroceryAmount = nonGroceryAmount - (nonGroceryAmount * (percentageDiscount / 100));

        // Flat discount of $5 for every $100 on total bill
        double flatDiscount = Math.floor(totalAmount / 100) * 5;

        // Final bill amount after discounts
        double finalAmount = discountedNonGroceryAmount + groceryAmount - flatDiscount;

        log.info("Final amount before conversion: {}", finalAmount);

        // Convert to target currency
        return convertCurrency(bill.getOriginalCurrency(), bill.getTargetCurrency(), finalAmount);
    }

    private double getApplicableDiscount(UserType user) {
        if (user.isEmployee()) {
            return 30.0;
        } else if (user.isAffiliate()) {
            return 10.0;
        } else if (user.isLoyalCustomer()) {
            return 5.0;
        }
        return 0.0;
    }

    private double convertCurrency(String baseCurrency, String targetCurrency, double amount) {
        Optional<Double> exchangeRateOpt = currencyExchangeService.getExchangeRate(baseCurrency, targetCurrency);

        double exchangeRate = exchangeRateOpt.orElseThrow(() ->
                new RuntimeException("Exchange rate not available for " + baseCurrency + " to " + targetCurrency));

        double convertedAmount = amount * exchangeRate;
        log.info("Converted amount: {} {} -> {} {}", amount, baseCurrency, convertedAmount, targetCurrency);
        return convertedAmount;
    }
}
