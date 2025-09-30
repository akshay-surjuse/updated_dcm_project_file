package com.digital.controller;

import com.digital.dto.EmailDto;
import com.digital.dto.ManagerStatusDto;
import com.digital.dto.ResetPasswordDto;
import com.digital.entity.User;
import com.digital.servicei.UserServiceI;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceI userServiceI;

    public UserController(UserServiceI userServiceI) {
        this.userServiceI = userServiceI;
    }

    /**
     * Create new user (ADMIN only).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createNewUser(@Valid @RequestBody User user) {
        String response = userServiceI.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Forgot password → send OTP to email.
     */
    @PostMapping("/otp")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailDto emailDto) {
        String response = userServiceI.sendOtp(emailDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Reset password with OTP.
     */
    @PutMapping("/password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        String response = userServiceI.resetPassword(resetPasswordDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Update user status (ADMIN only).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{userId}")
    public ResponseEntity<String> manageUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody ManagerStatusDto manageStatusDto
    ) {
        String response = userServiceI.manageUserStatus(userId, manageStatusDto);
        return ResponseEntity.ok(response);
    }

}
