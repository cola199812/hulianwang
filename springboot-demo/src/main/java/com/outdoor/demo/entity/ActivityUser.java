package com.outdoor.demo.entity;

/**
 * 活动-用户关联实体类
 * 对应数据库中的activity_user表，记录用户与活动的报名关系。
 */
public class ActivityUser {
    /** 关联记录ID */
    private Long id;
    /** 活动ID */
    private Long activityId;
    /** 用户ID */
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

