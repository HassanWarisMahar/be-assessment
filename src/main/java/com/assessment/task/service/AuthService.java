package com.assessment.task.service;

import com.assessment.task.repository.UserRepository;
import com.assessment.task.security.JwtUtils;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtil;

    public String authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
            return jwtUtil.generateToken(userDetails.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Invalid Credentials: " + e.getMessage());
        }
    }
}
