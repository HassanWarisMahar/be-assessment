package com.assessment.task.assessment;

import com.assessment.task.dto.Bill;
import com.assessment.task.dto.Item;
import com.assessment.task.dto.UserType;
import com.assessment.task.security.JwtUtils;
import com.assessment.task.service.AuthService;
import com.assessment.task.service.CurrencyExchangeService;
import com.assessment.task.service.DiscountService;
import com.assessment.task.service.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @Mock
    private CurrencyExchangeService currencyExchangeService;

    @InjectMocks
    private AuthService authService;

    @InjectMocks
    private DiscountService discountService;

    private UserType employee;
    private Bill bill;

    @BeforeEach
    void setUp() {
        employee = new UserType("EMPLOYEE", LocalDate.now().minusYears(3));
        bill = new Bill(List.of(new Item("Laptop", "ELECTRONICS", 1000.0)), "USD", "EUR");
    }

    @Test
    void testAuthenticateSuccess() {
        String username = "user1";
        String password = "password";
        String token = "mockedToken";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(customUserDetailService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtils.generateToken(username)).thenReturn(token);

        doNothing().when(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(username, password));

        String generatedToken = authService.authenticate(username, password);
        assertEquals(token, generatedToken);
    }

    @Test
    void testAuthenticateFailure() {
        doThrow(new RuntimeException("Invalid Credentials")).when(authenticationManager).authenticate(any());
        assertThrows(RuntimeException.class, () -> authService.authenticate("user", "wrongpassword"));
    }

    @Test
    void testDiscountForEmployee() {
        when(currencyExchangeService.getExchangeRate("USD", "EUR")).thenReturn(Optional.of(0.85));
        double finalAmount = discountService.calculateFinalAmount(employee, bill);
        assertEquals(595.0, finalAmount, 0.01);
    }

    @Test
    void testExchangeRateConversion() {
        when(currencyExchangeService.getExchangeRate("USD", "EUR")).thenReturn(Optional.of(0.85));
        double convertedAmount = discountService.calculateFinalAmount(employee, bill);
        assertNotEquals(1000.0, convertedAmount);
    }
}
