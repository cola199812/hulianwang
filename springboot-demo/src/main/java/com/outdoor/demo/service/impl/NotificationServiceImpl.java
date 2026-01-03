package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Notification;
import com.outdoor.demo.mapper.NotificationMapper;
import com.outdoor.demo.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional
    public Long create(Notification notification) {
        if (notification.getCreateTime() == null) {
            notification.setCreateTime(LocalDateTime.now());
        }
        if (notification.getRead() == null) {
            notification.setRead(false);
        }
        notificationMapper.insert(notification);
        return notification.getId();
    }

    @Override
    public List<Map<String, Object>> listByUserId(Long userId) {
        List<Notification> notifications = notificationMapper.findByUserId(userId);
        return convertToMapList(notifications);
    }

    @Override
    public boolean markAsRead(Long id) {
        return notificationMapper.markAsRead(id) > 0;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        return notificationMapper.markAllAsRead(userId) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return notificationMapper.delete(id) > 0;
    }

    @Override
    public List<Map<String, Object>> listByUserIdAndType(Long userId, String type) {
        List<Notification> notifications = notificationMapper.findByUserIdAndType(userId, type);
        return convertToMapList(notifications);
    }

    @Override
    public void createActivityNotification(Long userId, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("activity");
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRead(false);
        create(notification);
    }

    private List<Map<String, Object>> convertToMapList(List<Notification> notifications) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Notification n : notifications) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId());
            map.put("userId", n.getUserId());
            map.put("type", n.getType());
            map.put("title", n.getTitle());
            map.put("content", n.getContent());
            map.put("read", n.getRead());
            map.put("createdAt", n.getCreateTime());
            result.add(map);
        }
        return result;
    }
}