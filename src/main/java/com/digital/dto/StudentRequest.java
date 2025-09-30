package com.digital.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequest {

    private Long userId;  // Existing User ID (already created)

    @NotBlank(message = "Roll number is required")
    @Pattern(regexp = "^[A-Z0-9]{5,15}$", message = "Roll number must be alphanumeric (5–15 chars)")
    private String rollNumber;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    private String street;
    private String city;
    private String state;
    private String country;

    @Pattern(regexp = "^[0-9]{5,10}$", message = "Pin code must be 5–10 digits")
    private String pinCode;

    @NotNull(message = "Class ID is required")
    private Long classId;
    private String admissionNumber;
    private String academicYear;

    @NotNull(message = "Section ID is required")
    private Long sectionId;
}
