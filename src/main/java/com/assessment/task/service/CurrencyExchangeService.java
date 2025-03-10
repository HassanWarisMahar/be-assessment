package com.assessment.task.service;

import com.assessment.task.dto.response.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyExchangeService {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    @Autowired
    public CurrencyExchangeService(RestTemplateBuilder restTemplateBuilder,
                                   @Value("${exchange.rate.api.url}") String apiUrl,
                                   @Value("${exchange.rate.api.key}") String apiKey) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public Double getExchangeRate(String baseCurrency, String targetCurrency) {
        String url = apiUrl + apiKey + "/latest/" + baseCurrency;

        try {
            ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(url, ExchangeRateResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getConversionRates().get(targetCurrency);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching exchange rate: " + e.getMessage());
        }
        return null;
    }
}
