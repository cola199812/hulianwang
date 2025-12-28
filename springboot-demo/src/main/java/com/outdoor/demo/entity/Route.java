package com.outdoor.demo.entity;

/**
 * 路线实体类
 * 对应数据库中的route表，存储户外路线信息。
 */
public class Route {
    /** 路线ID */
    private Long id;
    /** 路线名称 */
    private String name;
    /** 路线距离（公里） */
    private Double distance;
    /** 难度等级（如：简单、中等、困难） */
    private String level;
    /** 路线描述 */
    private String description;
    /** 创建者ID */
    private Long creatorId;

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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}

