package com.outdoor.demo.controller;

import com.outdoor.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知控制器
 * 处理通知相关的HTTP请求
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 获取当前用户的通知列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getNotifications(HttpSession session, @RequestParam(required = false) String type) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseEntity.status(401).body(buildResponse("未登录"));
        }

        try {
            if (type != null && !type.isEmpty()) {
                return ResponseEntity.ok(notificationService.getNotificationsByUserIdAndType(userId, type));
            } else {
                return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(buildResponse("获取通知失败"));
        }
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(HttpSession session, @PathVariable Long id) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseEntity.status(401).body(buildResponse("未登录"));
        }

        try {
            boolean success = notificationService.markAsRead(id, userId);
            if (success) {
                return ResponseEntity.ok(buildResponse("标记成功"));
            } else {
                return ResponseEntity.badRequest().body(buildResponse("通知不存在或已删除"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(buildResponse("操作失败"));
        }
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseEntity.status(401).body(buildResponse("未登录"));
        }

        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(buildResponse("全部标记为已读成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(buildResponse("操作失败"));
        }
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(HttpSession session, @PathVariable Long id) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseEntity.status(401).body(buildResponse("未登录"));
        }

        try {
            boolean success = notificationService.deleteNotification(id, userId);
            if (success) {
                return ResponseEntity.ok(buildResponse("删除成功"));
            } else {
                return ResponseEntity.badRequest().body(buildResponse("通知不存在或已删除"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(buildResponse("操作失败"));
        }
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return ResponseEntity.status(401).body(buildResponse("未登录"));
        }

        try {
            int count = notificationService.getUnreadCount(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(buildResponse("获取未读数量失败"));
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId instanceof Long ? (Long) userId : null;
    }

    /**
     * 构建响应体
     */
    private Map<String, Object> buildResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}