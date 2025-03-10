package com.assessment.task.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Setter
@Getter
public class ExchangeRateResponse{

    private String baseCode;
    private Map<String, Double> conversionRates;

}


