package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 活动实体类
 * 对应数据库中的activity表，存储活动的基本信息。
 */
public class Activity {
    /** 活动ID */
    private Long id;
    /** 活动名称 */
    private String name;
    /** 关联的路线ID */
    private Long routeId;
    /** 活动时间 */
    private LocalDateTime time;
    /** 最大参与人数 */
    private Integer maxPeople;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Integer getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(Integer maxPeople) {
        this.maxPeople = maxPeople;
    }
}

