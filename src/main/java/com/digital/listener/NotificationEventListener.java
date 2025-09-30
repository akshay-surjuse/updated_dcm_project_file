package com.digital.listener;

import com.digital.entity.ExamNotification;
import com.digital.enums.NotificationType;
import com.digital.events.ExamCreatedEvent;
import com.digital.events.ReportCardGeneratedEvent;
import com.digital.events.ResultPublishedEvent;
import com.digital.repository.ExamNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final ExamNotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleExamCreated(ExamCreatedEvent event) {
        String title = "New Exam Scheduled";
        String message = "A new exam has been scheduled. Exam ID: " + event.examId();

        event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.EXAM, event.examId()));
        event.teacherIds().forEach(id -> sendNotification(id, title, message, NotificationType.EXAM, event.examId()));
    }

    @EventListener
    public void handleResultPublished(ResultPublishedEvent event) {
        String title = "Result Published";
        String message = "Your exam results are now available. Result ID: " + event.resultId();

        event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.RESULT, event.resultId()));
    }

    @EventListener
    public void handleReportCardGenerated(ReportCardGeneratedEvent event) {
        String title = "Report Card Available";
        String message = "Your report card is now available. ReportCard ID: " + event.reportCardId();

        event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.REPORT_CARD, event.reportCardId()));
    }

    private void sendNotification(Long userId, String title, String message, NotificationType type, Long refId) {
        ExamNotification notification = ExamNotification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(refId)
                .build();

        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/user/" + userId, notification);
    }
}