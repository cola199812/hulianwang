package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Activity;
import com.outdoor.demo.entity.ActivityUser;
import com.outdoor.demo.mapper.ActivityMapper;
import com.outdoor.demo.mapper.ActivityUserMapper;
import com.outdoor.demo.service.ActivityService;
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

    public ActivityServiceImpl(ActivityMapper activityMapper, ActivityUserMapper activityUserMapper) {
        this.activityMapper = activityMapper;
        this.activityUserMapper = activityUserMapper;
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
        return activityUserMapper.insert(au) > 0;
    }
}

