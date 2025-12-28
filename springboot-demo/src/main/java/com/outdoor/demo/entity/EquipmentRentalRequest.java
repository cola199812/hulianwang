package com.outdoor.demo.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * 装备租赁请求实体类
 * 用于接收前端发来的装备租赁请求数据。
 */
public class EquipmentRentalRequest {
    /** 装备ID */
    @NotNull(message = "装备ID不能为空")
    private Long equipmentId;
    
    /** 租赁开始时间（格式：yyyy-MM-dd HH:mm:ss） */
    @NotNull(message = "租赁开始时间不能为空")
    @Size(min = 19, max = 19, message = "时间格式必须为yyyy-MM-dd HH:mm:ss")
    private String startTime;
    
    /** 租赁结束时间（格式：yyyy-MM-dd HH:mm:ss） */
    @NotNull(message = "租赁结束时间不能为空")
    @Size(min = 19, max = 19, message = "时间格式必须为yyyy-MM-dd HH:mm:ss")
    private String endTime;
    
    /** 租赁天数（前端计算，后端验证） */
    @NotNull(message = "租赁天数不能为空")
    @Positive(message = "租赁天数必须大于0")
    private Integer rentalDays;
    
    /** 联系电话 */
    @NotNull(message = "联系电话不能为空")
    @Size(min = 11, max = 11, message = "联系电话必须为11位")
    private String contactPhone;

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}