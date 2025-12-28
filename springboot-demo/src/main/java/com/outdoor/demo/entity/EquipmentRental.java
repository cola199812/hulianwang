package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 装备租赁实体类
 * 对应数据库中的equipment_rental表，存储装备租赁记录。
 */
public class EquipmentRental {
    /** 租赁ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 装备ID */
    private Long equipmentId;
    /** 租赁开始时间 */
    private LocalDateTime startTime;
    /** 租赁结束时间 */
    private LocalDateTime endTime;
    /** 租赁天数 */
    private Integer rentalDays;
    /** 每日租金 */
    private Double dailyRentalPrice;
    /** 押金 */
    private Double deposit;
    /** 总租金 */
    private Double totalPrice;
    /** 支付状态（未支付、已支付、已退款） */
    private String paymentStatus;
    /** 租赁状态（预订中、已出租、已归还、已取消） */
    private String rentalStatus;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 联系电话 */
    private String contactPhone;

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

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
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

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}