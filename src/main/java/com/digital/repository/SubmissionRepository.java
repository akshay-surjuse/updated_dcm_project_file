package com.digital.repository;

import com.digital.entity.ExamSubmission;
import com.digital.entity.Exam;
import com.digital.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<ExamSubmission, Long> {
    // Fetch all submissions for a given examId
    List<ExamSubmission> findByExam_ExamId(Long examId);

    // Fetch submission for a given examId and studentId
    // In SubmissionRepository
    Optional<ExamSubmission> findByExam_ExamIdAndStudent_StudentRegId(Long examId, Long studentId);
}
