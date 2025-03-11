package com.assessment.task.controller;

import com.assessment.task.dto.Bill;
import com.assessment.task.dto.UserType;
import com.assessment.task.dto.request.BillRequest;
import com.assessment.task.service.DiscountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
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
        log.info("Received request to calculate bill for userType: {} with registrationDate: {}",
                request.getUserType(), request.getRegistrationDate());

        log.debug("Request Details: Items = {}, OriginalCurrency = {}, TargetCurrency = {}",
                request.getItems(), request.getOriginalCurrency(), request.getTargetCurrency());

        try {
            UserType user = new UserType(request.getUserType(), request.getRegistrationDate());
            Bill bill = new Bill(request.getItems(), request.getOriginalCurrency(), request.getTargetCurrency());

            double finalAmount = discountService.calculateFinalAmount(user, bill);
            log.info("Calculated payable amount: {} for userType: {}", finalAmount, request.getUserType());

            return ResponseEntity.ok(Map.of("payableAmount", finalAmount));
        } catch (Exception e) {
            log.error("Error occurred while calculating the bill", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
}
