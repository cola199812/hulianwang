package com.outdoor.demo.service;

import com.outdoor.demo.entity.Equipment;

import java.util.List;

/**
 * 装备服务接口
 * 定义装备租赁相关的业务逻辑方法
 */
public interface EquipmentService {
    /**
     * 获取所有可租赁装备
     * @return 装备列表
     */
    List<Equipment> getAllAvailableEquipments();

    /**
     * 根据类型获取可租赁装备
     * @param type 装备类型
     * @return 装备列表
     */
    List<Equipment> getEquipmentsByType(String type);

    /**
     * 根据ID获取装备详情
     * @param id 装备ID
     * @return 装备对象
     */
    Equipment getEquipmentById(Long id);

    /**
     * 减少装备库存
     * @param id 装备ID
     * @param quantity 减少数量
     * @return 是否成功
     */
    boolean decreaseStock(Long id, Integer quantity);

    /**
     * 增加装备库存
     * @param id 装备ID
     * @param quantity 增加数量
     * @return 是否成功
     */
    boolean increaseStock(Long id, Integer quantity);
}
