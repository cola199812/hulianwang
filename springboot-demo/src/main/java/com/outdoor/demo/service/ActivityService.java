package com.outdoor.demo.service;

import com.outdoor.demo.entity.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    Long create(Activity activity);
    List<Map<String, Object>> listWithCurrentPeople();
    boolean join(Long activityId, Long userId);
    
    List<Map<String, Object>> listMyActivities(Long creatorId);
    void updateActivity(Long id, Activity activity);
    void deleteActivity(Long id);
    boolean cancelJoin(Long activityId, Long userId);
    void notifyActivityStart(Long activityId);
}

