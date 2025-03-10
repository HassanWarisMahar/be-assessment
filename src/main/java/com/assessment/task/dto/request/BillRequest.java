package com.assessment.task.dto.request;

import com.assessment.task.dto.Item;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Data
public class BillRequest {
    private String userType;
    private LocalDate registrationDate;
    private List<Item> items;
    private String originalCurrency;
    private String targetCurrency;

}

