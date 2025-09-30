package com.digital.repository;




import com.digital.entity.Notification;
import com.digital.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTeacher(Teacher teacher);
}

