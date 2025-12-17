package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.ActivityUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ActivityUserMapper {
    int insert(ActivityUser au);
    int countByActivityId(@Param("activityId") Long activityId);
    ActivityUser findByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);
}

