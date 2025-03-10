package com.assessment.task.controller;

import com.assessment.task.dto.request.AuthRequest;
import com.assessment.task.dto.response.AuthResponse;
import com.assessment.task.service.AuthService;
import com.assessment.task.service.CurrencyExchangeService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    CurrencyExchangeService currencyExchangeService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
    @GetMapping("/welcome")
    public ResponseEntity<String> wellcome() {

        return ResponseEntity.ok("Welcome");
    }

    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(@RequestParam String baseCurrency, @RequestParam String targetCurrency) {
        Double exchangeRate = currencyExchangeService.getExchangeRate(baseCurrency, targetCurrency);
        return exchangeRate != null
                ? ResponseEntity.ok(Map.of("exchangeRate", exchangeRate))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error fetching exchange rate");
    }
}




