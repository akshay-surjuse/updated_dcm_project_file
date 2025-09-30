package com.digital.serviceimpl;

import com.digital.dto.NotificationDto;
import com.digital.entity.Notification;
import com.digital.entity.Teacher;
import com.digital.repository.NotificationRepository;
import com.digital.repository.TeacherRepository;
import com.digital.servicei.NotificationService;
import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final TeacherRepository teacherRepository;
 private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<NotificationDto> getMyNotifications() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return notificationRepository.findByTeacher(teacher)
                .stream()
                .map(n -> NotificationDto.builder()
                        .id(n.getId())
                        .message(n.getMessage())
                        .type(n.getType())
                        .createdAt(n.getCreatedAt())
                        .status(n.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void sendNotification(Long teacherId, String message) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Notification notification = Notification.builder()
                .teacher(teacher)
                .message(message)
                .type("ADMIN")
                .status("UNREAD")
                .createdAt(java.time.LocalDateTime.now())
                .build();

        notificationRepository.save(notification);


       messagingTemplate.convertAndSend("/topic/teacher-" + teacherId, message);
    }
    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notification not found"));
        if (!"READ".equalsIgnoreCase(notification.getStatus())) {
            notification.setStatus("READ");
            notificationRepository.save(notification);
        }

    }}