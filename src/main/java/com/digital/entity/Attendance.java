package com.digital.entity;

import com.digital.enums.AttendanceStatus;
import com.digital.enums.MarkBy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@JsonIgnoreProperties({"schoolClass", "session", "student"})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // ✅ Corrected
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classId", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessionId", nullable = false)
    private Session session;

    @Column(nullable = false)
    private LocalDate date;

    //@Column(nullable = false) // It should be null for auto mark absent api
    private LocalDateTime joinTime; // (student check-in)

    private LocalDateTime exitTime; // (student check-out)

    private Long durationMinutes; //(auto-calculated)

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    @Enumerated(EnumType.STRING)
    private MarkBy markedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void setUpdated(){
        this.updatedAt = LocalDateTime.now();
    }
}
