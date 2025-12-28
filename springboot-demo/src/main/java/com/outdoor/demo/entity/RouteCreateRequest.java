package com.outdoor.demo.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 路线创建请求对象
 * 接收创建路线时的参数。
 */
public class RouteCreateRequest {
    /** 路线名称 */
    @NotBlank
    private String name;
    /** 路线距离（公里） */
    @NotNull
    private Double distance;
    /** 难度等级 */
    @NotBlank
    private String level;
    /** 路线描述 */
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

