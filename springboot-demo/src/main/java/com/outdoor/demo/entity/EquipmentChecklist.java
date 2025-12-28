package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 装备清单实体类
 * 对应数据库中的equipment_checklist表，存储装备清单信息。
 */
public class EquipmentChecklist {
    /** 清单ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 清单名称 */
    private String name;
    /** 路线类型（如：徒步、登山、露营等） */
    private String routeType;
    /** 天气情况（如：晴天、雨天、雪天等） */
    private String weather;
    /** 创建时间 */
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}