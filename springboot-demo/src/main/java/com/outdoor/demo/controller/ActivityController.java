package com.outdoor.demo.controller;

import com.outdoor.demo.entity.Activity;
import com.outdoor.demo.entity.ActivityCreateRequest;
import com.outdoor.demo.entity.ActivityJoinRequest;
import com.outdoor.demo.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
@Validated
public class ActivityController {
    private final ActivityService activityService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody ActivityCreateRequest req) {
        Activity a = new Activity();
        a.setName(req.getName());
        a.setRouteId(req.getRouteId());
        a.setTime(LocalDateTime.parse(req.getTime(), formatter));
        a.setMaxPeople(req.getMaxPeople());
        Long id = activityService.create(a);
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("message", "创建成功");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<Map<String, Object>> list = activityService.listWithCurrentPeople();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody ActivityJoinRequest req, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }
        boolean ok = activityService.join(req.getActivityId(), (Long) uid);
        Map<String, Object> body = new HashMap<>();
        if (ok) {
            body.put("message", "报名成功");
            return ResponseEntity.ok(body);
        } else {
            body.put("message", "报名失败，人数已满或已报名");
            return ResponseEntity.badRequest().body(body);
        }
    }
}

