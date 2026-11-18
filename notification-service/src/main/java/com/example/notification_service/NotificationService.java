package com.example.notification_service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepo notificationRepo;

    public NotificationService(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public Notification createNotification(Notification notification) {
        return notificationRepo.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepo.findByUserId(userId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepo.findAll();
    }

    public void deleteNotification(Long id) {
        notificationRepo.deleteById(id);
    }
}
