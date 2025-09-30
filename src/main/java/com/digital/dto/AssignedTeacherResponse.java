package com.digital.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedTeacherResponse {
    private Long id;          // Teacher table primary key
    private Long classId;     // primary classId or first class
    private List<String> className;  // list of class names
    private Long sectionId;   // primary sectionId or first section
    private List<String> sectionName; // list of section names
    private Long teacherId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime assignedAt;
   // private List<Long> assignedSectionIds; // optional: raw section IDs
}
