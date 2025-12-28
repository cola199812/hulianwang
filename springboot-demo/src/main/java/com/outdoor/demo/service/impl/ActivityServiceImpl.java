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
/**
 * 活动服务实现类
 * 实现活动相关的业务逻辑。
 */
public class ActivityServiceImpl implements ActivityService {
    private final ActivityMapper activityMapper;
    private final ActivityUserMapper activityUserMapper;

    public ActivityServiceImpl(ActivityMapper activityMapper, ActivityUserMapper activityUserMapper) {
        this.activityMapper = activityMapper;
        this.activityUserMapper = activityUserMapper;
    }

    @Override
    @Transactional
    /**
     * 创建活动
     * 将活动信息插入数据库。
     */
    public Long create(Activity activity) {
        activityMapper.insert(activity);
        return activity.getId();
    }

    @Override
    /**
     * 获取活动列表
     * 查询所有活动，并统计每个活动的当前报名人数。
     */
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
    /**
     * 报名活动
     * 检查活动是否存在、人数是否已满、用户是否已报名，然后插入报名记录。
     */
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

