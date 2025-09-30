package com.digital.dto;

import com.digital.entity.Subject;
import com.digital.entity.Teacher;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UpdateTimetableRequest {

    private Teacher teacher;

    private Subject subject;

    private String topic;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
