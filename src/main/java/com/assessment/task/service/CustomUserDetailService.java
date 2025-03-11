package com.assessment.task.service;

import com.assessment.task.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.assessment.task.model.User user = userRepository.findByUsername(username);
        // You can replace this with a DB lookup
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new User(user.getUsername(), user.getPassword(),new ArrayList<>());

    }

    @PostConstruct
    private void createUsers(){

        com.assessment.task.model.User user = new com.assessment.task.model.User();
        user.setUsername("hassan_waris");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }
}
