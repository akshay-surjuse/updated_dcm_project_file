package com.digital.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmissionRequest {
    private List<AnswerDto> answers; // [{questionId, answer}]
}


