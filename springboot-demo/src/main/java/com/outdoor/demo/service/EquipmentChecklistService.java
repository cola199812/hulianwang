package com.outdoor.demo.service;

import com.outdoor.demo.entity.EquipmentChecklist;
import com.outdoor.demo.entity.EquipmentChecklistItem;

import java.util.List;

/**
 * 装备清单服务接口
 * 定义装备清单相关的业务逻辑方法
 */
public interface EquipmentChecklistService {
    /**
     * 创建智能装备清单
     * @param userId 用户ID
     * @param name 清单名称
     * @param routeType 路线类型
     * @param weather 天气情况
     * @return 装备清单对象
     */
    EquipmentChecklist createSmartChecklist(Long userId, String name, String routeType, String weather);

    /**
     * 根据ID获取装备清单
     * @param id 清单ID
     * @return 装备清单对象
     */
    EquipmentChecklist getChecklistById(Long id);

    /**
     * 根据用户ID获取装备清单列表
     * @param userId 用户ID
     * @return 装备清单列表
     */
    List<EquipmentChecklist> getChecklistsByUserId(Long userId);

    /**
     * 更新清单名称
     * @param id 清单ID
     * @param name 新名称
     * @return 是否成功
     */
    boolean updateChecklistName(Long id, String name);

    /**
     * 删除装备清单
     * @param id 清单ID
     * @return 是否成功
     */
    boolean deleteChecklist(Long id);

    /**
     * 添加清单项目
     * @param checklistId 清单ID
     * @param equipmentId 装备ID（可选）
     * @param customName 自定义名称（可选）
     * @param quantity 数量
     * @return 清单项目对象
     */
    EquipmentChecklistItem addChecklistItem(Long checklistId, Long equipmentId, String customName, Integer quantity);

    /**
     * 更新项目打包状态
     * @param itemId 项目ID
     * @param isPacked 是否已打包
     * @return 是否成功
     */
    boolean updateItemPackedStatus(Long itemId, Boolean isPacked);

    /**
     * 更新项目数量
     * @param itemId 项目ID
     * @param quantity 新数量
     * @return 是否成功
     */
    boolean updateItemQuantity(Long itemId, Integer quantity);

    /**
     * 删除清单项目
     * @param itemId 项目ID
     * @return 是否成功
     */
    boolean deleteChecklistItem(Long itemId);

    /**
     * 获取清单所有项目
     * @param checklistId 清单ID
     * @return 清单项目列表
     */
    List<EquipmentChecklistItem> getChecklistItems(Long checklistId);
}
