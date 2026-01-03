package com.outdoor.demo.service;

import com.outdoor.demo.entity.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    Long create(Notification notification);
    List<Map<String, Object>> listByUserId(Long userId);
    boolean markAsRead(Long id);
    boolean markAllAsRead(Long userId);
    boolean delete(Long id);
    List<Map<String, Object>> listByUserIdAndType(Long userId, String type);
    void createActivityNotification(Long userId, String title, String content);
}