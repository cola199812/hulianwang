package com.outdoor.demo.entity;

/**
 * 装备清单项实体类
 * 对应数据库中的equipment_checklist_item表，存储装备清单中的具体物品。
 */
public class EquipmentChecklistItem {
    /** 清单项ID */
    private Long id;
    /** 清单ID */
    private Long checklistId;
    /** 装备ID（如果是系统装备） */
    private Long equipmentId;
    /** 物品名称（如果是自定义物品） */
    private String itemName;
    /** 自定义名称（用于前端显示） */
    private String customName;
    /** 物品类型 */
    private String itemType;
    /** 数量 */
    private Integer quantity;
    /** 是否已携带 */
    private Boolean isPacked;
    /** 是否是租赁装备 */
    private Boolean isRental;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(Long checklistId) {
        this.checklistId = checklistId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean isPacked() {
        return isPacked;
    }

    public void setPacked(Boolean packed) {
        isPacked = packed;
    }

    public Boolean isRental() {
        return isRental;
    }

    public void setRental(Boolean rental) {
        isRental = rental;
    }
}