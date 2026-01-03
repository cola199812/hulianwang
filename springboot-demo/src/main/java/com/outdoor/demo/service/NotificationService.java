package com.outdoor.demo.service;

import com.outdoor.demo.entity.Notification;
import com.outdoor.demo.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知服务类
 * 实现通知相关的业务逻辑
 */
@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    /**
     * 创建通知
     */
    public Notification createNotification(Long userId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(false);
        notificationMapper.insert(notification);
        return notification;
    }

    /**
     * 获取用户所有通知
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationMapper.getByUserId(userId);
    }

    /**
     * 获取用户指定类型的通知
     */
    public List<Notification> getNotificationsByUserIdAndType(Long userId, String type) {
        return notificationMapper.getByUserIdAndType(userId, type);
    }

    /**
     * 标记通知为已读
     */
    public boolean markAsRead(Long id, Long userId) {
        return notificationMapper.markAsRead(id, userId) > 0;
    }

    /**
     * 标记所有通知为已读
     */
    public boolean markAllAsRead(Long userId) {
        return notificationMapper.markAllAsRead(userId) > 0;
    }

    /**
     * 删除通知
     */
    public boolean deleteNotification(Long id, Long userId) {
        return notificationMapper.delete(id, userId) > 0;
    }

    /**
     * 获取未读通知数量
     */
    public int getUnreadCount(Long userId) {
        return notificationMapper.getUnreadCount(userId);
    }
}