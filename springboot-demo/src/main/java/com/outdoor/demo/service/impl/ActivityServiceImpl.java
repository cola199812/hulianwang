package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Activity;
import com.outdoor.demo.entity.ActivityUser;
import com.outdoor.demo.entity.User;
import com.outdoor.demo.mapper.ActivityMapper;
import com.outdoor.demo.mapper.ActivityUserMapper;
import com.outdoor.demo.mapper.UserMapper;
import com.outdoor.demo.service.ActivityService;
import com.outdoor.demo.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityMapper activityMapper;
    private final ActivityUserMapper activityUserMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    public ActivityServiceImpl(ActivityMapper activityMapper, ActivityUserMapper activityUserMapper, 
                             UserMapper userMapper, NotificationService notificationService) {
        this.activityMapper = activityMapper;
        this.activityUserMapper = activityUserMapper;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Long create(Activity activity) {
        activityMapper.insert(activity);
        return activity.getId();
    }

    @Override
    public List<Map<String, Object>> listWithCurrentPeople() {
        List<Activity> list = activityMapper.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Activity a : list) {
            int count = activityUserMapper.countByActivityId(a.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("name", a.getName());
            map.put("routeId", a.getRouteId());
            map.put("time", a.getTime());
            map.put("maxPeople", a.getMaxPeople());
            map.put("currentPeople", count);
            result.add(map);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean join(Long activityId, Long userId) {
        Activity a = activityMapper.findById(activityId);
        if (a == null) {
            return false;
        }
        int count = activityUserMapper.countByActivityId(activityId);
        if (count >= a.getMaxPeople()) {
            return false;
        }
        ActivityUser existing = activityUserMapper.findByActivityIdAndUserId(activityId, userId);
        if (existing != null) {
            return false;
        }
        ActivityUser au = new ActivityUser();
        au.setActivityId(activityId);
        au.setUserId(userId);
        boolean result = activityUserMapper.insert(au) > 0;
        
        if (result) {
            // 发送报名成功通知
            notificationService.createActivityNotification(userId, "报名成功", "您已成功报名活动: " + a.getName());
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> listMyActivities(Long creatorId) {
        List<Activity> list = activityMapper.findMyActivities(creatorId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Activity a : list) {
            int count = activityUserMapper.countByActivityId(a.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("name", a.getName());
            map.put("routeId", a.getRouteId());
            map.put("time", a.getTime());
            map.put("maxPeople", a.getMaxPeople());
            map.put("currentPeople", count);
            result.add(map);
        }
        return result;
    }

    @Override
    @Transactional
    public void updateActivity(Long id, Activity activity) {
        activity.setId(id);
        activityMapper.update(activity);
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        activityMapper.deleteById(id);
    }

    @Override
    @Transactional
    public boolean cancelJoin(Long activityId, Long userId) {
        ActivityUser existing = activityUserMapper.findByActivityIdAndUserId(activityId, userId);
        if (existing == null) {
            return false; // 未报名，无法取消
        }
        boolean result = activityUserMapper.delete(existing.getId()) > 0;
        
        if (result) {
            // 发送取消报名通知
            Activity activity = activityMapper.findById(activityId);
            if (activity != null) {
                notificationService.createActivityNotification(userId, "取消报名", "您已取消报名活动: " + activity.getName());
            }
        }
        
        return result;
    }

    @Override
    public void notifyActivityStart(Long activityId) {
        Activity activity = activityMapper.findById(activityId);
        if (activity == null) {
            return;
        }
        
        // 获取所有报名该活动的用户
        List<ActivityUser> participants = activityUserMapper.findByActivityId(activityId);
        
        for (ActivityUser participant : participants) {
            String title = "活动即将开始";
            String content = String.format("活动\"%s\"将在\"%s\"开始，请准时参加！", 
                                        activity.getName(), activity.getTime());
            notificationService.createActivityNotification(participant.getUserId(), title, content);
        }
    }
    
    @Override
    public List<Long> listJoinedActivityIds(Long userId) {
        List<ActivityUser> list = activityUserMapper.findByUserId(userId);
        List<Long> ids = new ArrayList<>();
        for (ActivityUser au : list) {
            ids.add(au.getActivityId());
        }
        return ids;
    }
}

