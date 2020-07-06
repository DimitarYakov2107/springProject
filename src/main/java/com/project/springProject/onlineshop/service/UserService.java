package com.project.springProject.onlineshop.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.project.springProject.onlineshop.model.User;
import com.project.springProject.onlineshop.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);
    
    User save(UserRegistrationDto registration);
}
