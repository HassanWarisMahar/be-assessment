package com.assessment.task.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRateResponse {
    private String result;

    @JsonProperty("conversion_rates")
    private Map<String, Double> conversionRates;
}
