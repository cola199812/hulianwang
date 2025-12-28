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
/**
 * 【Controller层 - 控制器】
 * 这个类就像是网站的“前台接待员”。
 * 它的主要工作是：
 * 1. 接收前端（网页或APP）发来的请求（比如：创建活动、查询列表）。
 * 2. 检查请求带来的数据是否合法（比如：活动名字是不是空的）。
 * 3. 指挥 Service 层（业务逻辑层）去干活。
 * 4. 把干活的结果打包好，返回给前端。
 */
public class ActivityController {
    private final ActivityService activityService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * 创建活动
     * 接收前端发来的“创建活动”请求。
     *
     * @param req 前端传来的数据（包含活动名、时间、地点等）
     * @return 告诉前端创建成功了，并返回新活动的ID
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody ActivityCreateRequest req) {
        // ... (代码保持不变)
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

    /**
     * 获取活动列表
     * 接收前端发来的“查看活动列表”请求。
     * 这个接口会返回所有的活动，并且告诉你每个活动现在有多少人报名了。
     *
     * @return 包含所有活动信息的列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<Map<String, Object>> list = activityService.listWithCurrentPeople();
        return ResponseEntity.ok(list);
    }

    /**
     * 报名活动
     * 接收前端发来的“我要报名”请求。
     *
     * @param req 包含要报名的活动ID
     * @param session 用来识别当前是谁在登录（就像游乐园的门票）
     * @return 告诉前端报名是否成功
     */
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

