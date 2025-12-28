package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 装备实体类
 * 对应数据库中的equipment表，存储装备信息。
 */
public class Equipment {
    /** 装备ID */
    private Long id;
    /** 装备名称 */
    private String name;
    /** 装备类型 */
    private String type;
    /** 装备描述 */
    private String description;
    /** 装备重量（克） */
    private Integer weight;
    /** 装备图片URL */
    private String imageUrl;
    /** 租金（每天） */
    private Double dailyRentalPrice;
    /** 押金 */
    private Double deposit;
    /** 库存数量 */
    private Integer stock;
    /** 创建时间 */
    private LocalDateTime createTime;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getDailyRentalPrice() {
        return dailyRentalPrice;
    }

    public void setDailyRentalPrice(Double dailyRentalPrice) {
        this.dailyRentalPrice = dailyRentalPrice;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}