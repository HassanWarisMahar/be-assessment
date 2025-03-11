package com.assessment.task.service;

import com.assessment.task.dto.response.ExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
public class CurrencyExchangeService {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    @Autowired
    public CurrencyExchangeService(RestTemplate restTemplate,
                                   @Value("${exchange.rate.api.url}") String apiUrl,
                                   @Value("${exchange.rate.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }
    @Cacheable(value = "exchangeRates", key = "#baseCurrency + '-' + #targetCurrency")
    public Optional<Double> getExchangeRate(String baseCurrency, String targetCurrency) {
        String url = String.format("%s%s/latest/%s?symbols=%s", apiUrl, apiKey, baseCurrency, targetCurrency);
        log.info("Fetching exchange rate from: {}", url);

        try {
            ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(url, ExchangeRateResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ExchangeRateResponse exchangeRateResponse = response.getBody();

                if (exchangeRateResponse.getConversionRates() != null) {
                    return Optional.ofNullable(exchangeRateResponse.getConversionRates().get(targetCurrency));
                } else {
                    log.error("Conversion rates missing in API response.");
                    return Optional.empty();
                }
            } else {
                log.error("Failed to fetch exchange rates. Status Code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error fetching exchange rate: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }
}
