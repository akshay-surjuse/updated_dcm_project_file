package com.digital.serviceimpl;

import com.digital.dto.EmailDto;
import com.digital.dto.ManagerStatusDto;
import com.digital.dto.ResetPasswordDto;
import com.digital.entity.User;
import com.digital.enums.Action;
import com.digital.enums.Module;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.UserRepository;
import com.digital.servicei.AuditLogServiceI;
import com.digital.servicei.UserServiceI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserServiceImpl implements UserServiceI {

    @Value(value = "${spring.mail.username}")
    private String from;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender javaMailSender;

    private final AuditLogServiceI auditLogServiceI;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender, AuditLogServiceI auditLogServiceI) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.auditLogServiceI = auditLogServiceI;
    }

    @Override
    public String add(User user) {
        String normalPassword = user.getPassword();
       user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setPassword(user.getPassword());

        User savedUser = userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(savedUser.getEmail());
        message.setSubject("Digital Classroom Credentials");
        message.setText("Your Digital Classroom Credentials credentials are: \n\n" + "username: " + savedUser.getUsername() + "\n" + "password: " + normalPassword + "\n" + "Please reset the password before login");
        javaMailSender.send(message);

        return "User record created successfully.";
    }

    // This logic is used for internal use to update User's lastLogin value.

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    // This logic is used for internal use to get User record by username

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User with given username is not found in the database"));
    }

    @Override
    public String sendOtp(EmailDto emailDto) {
        User user = userRepository.findByEmail(emailDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Invalid email address"));

        String otp = String.format("%06d", new Random().nextInt(1000000));

        user.setOtp(passwordEncoder.encode(otp));
        user.setOtpGenerationTime(LocalDateTime.now());

        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Digital Classroom System OTP");
        message.setText("Your Digital Classroom System OTP is: " + otp);
        javaMailSender.send(message);

        return "Otp has been sent to given email";
    }

    @Override
    public String resetPassword(ResetPasswordDto resetPasswordDto) {

        User user = userRepository.findByEmail(resetPasswordDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Invalid email"));

        LocalDateTime otpExpiryTime = user.getOtpGenerationTime().plusMinutes(2);
        if(LocalDateTime.now().isAfter(otpExpiryTime))
            return "OTP is expired";

        if(passwordEncoder.matches(resetPasswordDto.getOtp(), user.getOtp())) {
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            user.setOtp(null);
            user.setOtpGenerationTime(null);

            userRepository.save(user);

            auditLogServiceI.logInfo(user.getUserId(), user.getUsername(), Action.PASSWORD_CHANGE, Module.USER_MODULE);

            return "Password reset successfully";
        }else
            return "Invalid OTP";
    }

    @Override
    public String manageUserStatus(Long userId, ManagerStatusDto manageStatusDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given ID is not found in the database"));

        user.setStatus(manageStatusDto.getStatus());
        userRepository.save(user);

        return "User status updated successfully";
    }
}
