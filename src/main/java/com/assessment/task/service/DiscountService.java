package com.assessment.task.service;

import com.assessment.task.dto.Bill;
import com.assessment.task.dto.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        double percentageDiscount = 0.0;
        if (user.isEmployee()) {
            percentageDiscount = 30.0;
        } else if (user.isAffiliate()) {
            percentageDiscount = 10.0;
        } else if (user.isLoyalCustomer()) {
            percentageDiscount = 5.0;
        }

        // Apply percentage discount only on non-grocery items
        double discountedNonGroceryAmount = nonGroceryAmount - (nonGroceryAmount * (percentageDiscount / 100));

        // Flat discount of $5 for every $100 on total bill
        double flatDiscount = Math.floor(totalAmount / 100) * 5;

        // Final bill amount after discounts
        double finalAmount = discountedNonGroceryAmount + groceryAmount - flatDiscount;

        // Convert to target currency
        return convertCurrency(bill.getOriginalCurrency(), bill.getTargetCurrency(), finalAmount);
    }

    private double convertCurrency(String baseCurrency, String targetCurrency, double amount) {
        double exchangeRate = currencyExchangeService.getExchangeRate(baseCurrency, targetCurrency);
        return amount * exchangeRate;
    }
}

