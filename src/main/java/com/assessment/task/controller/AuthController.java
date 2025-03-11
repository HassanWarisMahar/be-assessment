package com.assessment.task.controller;

import com.assessment.task.dto.request.AuthRequest;
import com.assessment.task.dto.response.AuthResponse;
import com.assessment.task.service.AuthService;
import com.assessment.task.service.CurrencyExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final CurrencyExchangeService currencyExchangeService;

    @Autowired
    public AuthController(AuthService authService, CurrencyExchangeService currencyExchangeService) {
        this.authService = authService;
        this.currencyExchangeService = currencyExchangeService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info("User attempting to login with username: {}", request.getUsername());

        try {
            String token = authService.authenticate(request.getUsername(), request.getPassword());
            log.info("User {} authenticated successfully", request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body(new AuthResponse("Authentication failed"));
        }
    }

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        log.info("Welcome endpoint accessed");
        return ResponseEntity.ok("Welcome");
    }

    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(@RequestParam String baseCurrency, @RequestParam String targetCurrency) {
        log.info("Received currency conversion request: {} -> {}", baseCurrency, targetCurrency);

        try {
            Optional<Double> exchangeRateOpt = currencyExchangeService.getExchangeRate(baseCurrency, targetCurrency);

            if (exchangeRateOpt.isPresent()) {
                log.info("Exchange rate found: {} -> {} = {}", baseCurrency, targetCurrency, exchangeRateOpt.get());
                return ResponseEntity.ok(Map.of("exchangeRate", exchangeRateOpt.get()));
            } else {
                log.warn("Exchange rate not available for {} to {}", baseCurrency, targetCurrency);
                return ResponseEntity.badRequest().body(Map.of("error", "Exchange rate not available for requested currencies"));
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching exchange rate for {} to {}", baseCurrency, targetCurrency, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
}
