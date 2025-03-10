package com.assessment.task.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Bill {

    private List<Item> items;
    private String originalCurrency;
    private String targetCurrency;


    public double getTotalAmount() {
        return items.stream().mapToDouble(Item::getPrice).sum();
    }

    public double getGroceryAmount() {
        return items.stream().filter(Item::isGrocery).mapToDouble(Item::getPrice).sum();
    }
}
