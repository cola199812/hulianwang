package com.outdoor.demo.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 活动创建请求对象
 * 接收创建活动时的参数。
 */
public class ActivityCreateRequest {
    /** 活动名称 */
    @NotBlank
    private String name;
    /** 关联的路线ID */
    @NotNull
    private Long routeId;
    /** 活动时间字符串，格式：yyyy-MM-dd HH:mm:ss */
    @NotBlank
    private String time; // 格式：yyyy-MM-dd HH:mm:ss
    /** 最大参与人数，至少为1 */
    @NotNull
    @Min(1)
    private Integer maxPeople;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(Integer maxPeople) {
        this.maxPeople = maxPeople;
    }
}

