package com.outdoor.demo.controller;

import com.outdoor.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@Validated
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        List<Map<String, Object>> list = notificationService.listByUserId((Long) uid);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/activity")
    public ResponseEntity<?> listActivityNotifications(HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        List<Map<String, Object>> list = notificationService.listByUserIdAndType((Long) uid, "activity");
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable("id") Long id, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        boolean success = notificationService.markAsRead(id);
        if (success) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "标记已读成功");
            return ResponseEntity.ok(body);
        } else {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "标记已读失败");
            return ResponseEntity.badRequest().body(body);
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        boolean success = notificationService.markAllAsRead((Long) uid);
        if (success) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "全部标记已读成功");
            return ResponseEntity.ok(body);
        } else {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "全部标记已读失败");
            return ResponseEntity.badRequest().body(body);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        boolean success = notificationService.delete(id);
        if (success) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "删除成功");
            return ResponseEntity.ok(body);
        } else {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "删除失败");
            return ResponseEntity.badRequest().body(body);
        }
    }
}