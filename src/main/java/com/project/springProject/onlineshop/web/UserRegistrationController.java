package com.project.springProject.onlineshop.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.springProject.onlineshop.constraint.FieldMatch;
import com.project.springProject.onlineshop.model.User;
import com.project.springProject.onlineshop.service.UserService;
import com.project.springProject.onlineshop.web.dto.UserRegistrationDto;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        return "registration";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
        BindingResult result) {
    	
    	// Required checks
    	if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty()) {
    		result.rejectValue("firstName", null, "First name is required.");
    	}
    	if (userDto.getLastName() == null || userDto.getLastName().isEmpty()) {
    		result.rejectValue("lastName", null, "Last name is required.");
    	}
    	if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
    		result.rejectValue("email", null, "Email is required.");
    	}
    	if (userDto.getConfirmEmail() == null || userDto.getConfirmEmail().isEmpty()) {
    		result.rejectValue("confirmEmail", null, "Confirm Email is required.");
    	}
    	if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
    		result.rejectValue("password", null, "Password is required.");
    	}
    	if (userDto.getConfirmPassword() == null || userDto.getConfirmPassword().isEmpty()) {
    		result.rejectValue("confirmPassword", null, "Confirm Password is required.");
    	}
    	
    	// Match checks
    	if (!userDto.getEmail().equals(userDto.getConfirmEmail())) {
    		result.rejectValue("email", null, "Email and Confirm Email should match.");
    		result.rejectValue("confirmEmail", null, "Email and Confirm Email should match.");
    	}
    	if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
    		result.rejectValue("password", null, "Password and Confirm Password should match.");
    		result.rejectValue("confirmPassword", null, "Password and Confirm Password should match.");
    	}

    	if (result.getFieldError("email") == null && result.getFieldError("confirmEmail") == null) {
            if (userService.findByEmail(userDto.getEmail()) != null) {
                result.rejectValue("email", null, "There is already an account registered with that email");
            }
    	}

        if (result.hasErrors()) {
            return "registration";
        }

        userService.save(userDto);
        return "redirect:/login";
    }
}