package com.outdoor.demo.entity;

import javax.validation.constraints.NotNull;

/**
 * 活动报名请求对象
 * 接收用户报名活动时的参数。
 */
public class ActivityJoinRequest {
    /** 要报名的活动ID */
    @NotNull
    private Long activityId;

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}

