package com.digital.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long holidayId;

    @NotNull(message = "Academic calender cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendarId", nullable = false)
    private AcademicCalendar calendar;

    @NotNull(message = "Holiday date cannot be null")
    @FutureOrPresent(message = "Holiday date must be from present or future")
    @Column(nullable = false)
    private LocalDate holidayDate;

    @NotBlank(message = "Holiday name cannot be null")
    @Column(nullable = false)
    private String holidayName; // (e.g., Independence Day)

    @Builder.Default
    @Column(nullable = false)
    private Boolean isEmergency = false; // (Boolean, default false)

    @Builder.Default
    @Column(nullable = false)
    private Boolean rescheduleRequired = false; // (Boolean, default false)

    @FutureOrPresent(message = "Reschedule date must be from present or future")
    private LocalDate rescheduledDate; // (Nullable Date)
}
