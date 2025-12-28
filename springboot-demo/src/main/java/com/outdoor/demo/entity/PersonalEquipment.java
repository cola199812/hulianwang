package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 个人装备实体类
 * 对应数据库中的personal_equipment表，存储用户的个人装备信息。
 */
public class PersonalEquipment {
    /** 装备ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 装备名称 */
    private String name;
    /** 装备类型 */
    private String type;
    /** 品牌 */
    private String brand;
    /** 购买日期 */
    private LocalDateTime purchaseDate;
    /** 购买价格 */
    private Double purchasePrice;
    /** 使用次数 */
    private Integer usageCount;
    /** 最后使用日期 */
    private LocalDateTime lastUsageDate;
    /** 下次维护日期 */
    private LocalDateTime nextMaintenanceDate;
    /** 装备状态（良好、需要维护、已损坏） */
    private String status;
    /** 装备图片URL */
    private String imageUrl;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 备注信息 */
    private String note;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public LocalDateTime getLastUsageDate() {
        return lastUsageDate;
    }

    public void setLastUsageDate(LocalDateTime lastUsageDate) {
        this.lastUsageDate = lastUsageDate;
    }

    public LocalDateTime getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDateTime nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}