package com.assessment.task.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class Item {
    private String name;
    private String category; // GROCERY, ELECTRONICS, CLOTHING, etc.
    private double price;

    public Item(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public boolean isGrocery() {
        return "GROCERY".equalsIgnoreCase(category);
    }
}

