package com.outdoor.demo.service;

import com.outdoor.demo.entity.EquipmentRental;
import com.outdoor.demo.entity.EquipmentRentalRequest;

import java.util.List;

/**
 * 装备租赁服务接口
 * 定义装备租赁记录相关的业务逻辑方法
 */
public interface EquipmentRentalService {
    /**
     * 创建装备租赁记录
     * @param request 租赁请求信息
     * @param userId 用户ID
     * @return 租赁记录对象
     */
    EquipmentRental createRental(EquipmentRentalRequest request, Long userId);

    /**
     * 根据ID获取租赁记录
     * @param id 租赁记录ID
     * @return 租赁记录对象
     */
    EquipmentRental getRentalById(Long id);

    /**
     * 根据用户ID获取租赁记录列表
     * @param userId 用户ID
     * @return 租赁记录列表
     */
    List<EquipmentRental> getRentalsByUserId(Long userId);

    /**
     * 完成支付
     * @param rentalId 租赁记录ID
     * @return 是否成功
     */
    boolean completePayment(Long rentalId);

    /**
     * 更新租赁状态
     * @param rentalId 租赁记录ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateRentalStatus(Long rentalId, String status);
}
