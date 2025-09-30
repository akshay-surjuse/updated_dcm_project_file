package com.digital.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectResponse {
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long classId;
    private String className;
    private Long teacherId;
    private String teacherName;
    private String description;
    private Boolean isActive;
}
