package com.digital.servicei;

import com.digital.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getMyNotifications();
    void sendNotification(Long teacherId, String message);
    void markAsRead(Long id);
}

