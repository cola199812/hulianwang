package com.outdoor.demo.service;

import com.outdoor.demo.entity.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    Long create(Activity activity);
    List<Map<String, Object>> listWithCurrentPeople();
    boolean join(Long activityId, Long userId);
}

