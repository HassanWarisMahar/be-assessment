package com.assessment.task.controller;

import com.assessment.task.dto.Bill;
import com.assessment.task.dto.UserType;
import com.assessment.task.dto.request.BillRequest;
import com.assessment.task.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class DiscountController {
    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateBill(@RequestBody BillRequest request) {
        UserType user = new UserType(request.getUserType(), request.getRegistrationDate());
        Bill bill = new Bill(request.getItems(), request.getOriginalCurrency(), request.getTargetCurrency());

        double finalAmount = discountService.calculateFinalAmount(user, bill);
        return ResponseEntity.ok(Map.of("payableAmount", finalAmount));
    }
}
