package com.assessment.task.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserType {

    private String userType; // EMPLOYEE, AFFILIATE, CUSTOMER
    private LocalDate registrationDate;

    public UserType (String userType, LocalDate registrationDate) {
        this.userType = userType;
        this.registrationDate = registrationDate;
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equalsIgnoreCase(userType);
    }

    public boolean isAffiliate() {
        return "AFFILIATE".equalsIgnoreCase(userType);
    }

    public boolean isLoyalCustomer() {
        return registrationDate.isBefore(LocalDate.now().minusYears(2));
    }
}

