package com.outdoor.demo.service;

import java.util.List;

import com.outdoor.demo.entity.PersonalEquipment;

/**
 * 个人装备服务接口
 * 定义个人装备管理相关的业务逻辑方法
 */
public interface PersonalEquipmentService {
    /**
     * 添加个人装备
     * @param userId 用户ID
     * @param name 装备名称
     * @param type 装备类型
     * @param purchaseDate 购买日期
     * @param note 备注信息
     * @return 个人装备对象
     */
    PersonalEquipment addPersonalEquipment(Long userId, String name, String type, String purchaseDate, String note);

    /**
     * 根据ID获取个人装备
     * @param id 装备ID
     * @return 个人装备对象
     */
    PersonalEquipment getPersonalEquipmentById(Long id);

    /**
     * 根据用户ID获取个人装备列表
     * @param userId 用户ID
     * @return 个人装备列表
     */
    List<PersonalEquipment> getPersonalEquipmentsByUserId(Long userId);

    /**
     * 根据类型获取个人装备
     * @param userId 用户ID
     * @param type 装备类型
     * @return 个人装备列表
     */
    List<PersonalEquipment> getPersonalEquipmentsByType(Long userId, String type);

    /**
     * 更新装备使用次数
     * @param id 装备ID
     * @return 是否成功
     */
    boolean increaseUsageCount(Long id);

    /**
     * 更新下次维护日期
     * @param id 装备ID
     * @param nextMaintenanceDate 下次维护日期
     * @return 是否成功
     */
    boolean updateNextMaintenanceDate(Long id, String nextMaintenanceDate);

    /**
     * 更新个人装备
     * @param id 装备ID
     * @param name 装备名称
     * @param type 装备类型
     * @return 是否成功
     */
    boolean updatePersonalEquipment(Long id, String name, String type);

    /**
     * 删除个人装备
     * @param id 装备ID
     * @return 是否成功
     */
    boolean deletePersonalEquipment(Long id);

    /**
     * 获取需要维护的装备列表
     * @param userId 用户ID
     * @return 需要维护的装备列表
     */
    List<PersonalEquipment> getEquipmentsNeedingMaintenance(Long userId);
}
