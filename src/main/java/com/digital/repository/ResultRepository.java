package com.digital.repository;

import com.digital.entity.Result;
import com.digital.enums.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByExamIdAndStudentId(Long examId, Long studentId);
    List<Result> findByStudentIdAndStatus(Long studentId, ResultStatus status);
    List<Result> findByExamId(Long examId);
    List<Result> findByStudentId(Long studentId);
    Optional<Result> findByStudentIdAndExamId(Long studentId, Long examId);
}