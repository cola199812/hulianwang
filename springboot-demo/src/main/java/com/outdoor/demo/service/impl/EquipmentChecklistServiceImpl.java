package com.outdoor.demo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outdoor.demo.entity.EquipmentChecklist;
import com.outdoor.demo.entity.EquipmentChecklistItem;
import com.outdoor.demo.mapper.EquipmentChecklistItemMapper;
import com.outdoor.demo.mapper.EquipmentChecklistMapper;
import com.outdoor.demo.service.EquipmentChecklistService;

@Service
/**
 * 装备清单服务实现类
 * 实现装备清单相关的业务逻辑，包括智能生成装备清单
 */
public class EquipmentChecklistServiceImpl implements EquipmentChecklistService {
    private final EquipmentChecklistMapper checklistMapper;
    private final EquipmentChecklistItemMapper checklistItemMapper;

    // 智能装备推荐规则
    private static final Map<String, List<String>> ROUTE_TYPE_EQUIPMENTS = new HashMap<>();
    private static final Map<String, List<String>> WEATHER_EQUIPMENTS = new HashMap<>();

    static {
        // 初始化路线类型对应的装备
        ROUTE_TYPE_EQUIPMENTS.put("山地徒步", List.of("登山鞋", "登山杖", "头灯", "背包", "水壶"));
        ROUTE_TYPE_EQUIPMENTS.put("森林探险", List.of("防水靴", "防虫喷雾", "指南针", "急救包", "手电筒"));
        ROUTE_TYPE_EQUIPMENTS.put("沙漠穿越", List.of("遮阳帽", "防晒霜", "水壶", "太阳镜", "防风沙面罩"));
        ROUTE_TYPE_EQUIPMENTS.put("水上活动", List.of("救生衣", "防水袋", "速干衣", "潜水镜", "浮潜鞋"));
        ROUTE_TYPE_EQUIPMENTS.put("城市徒步", List.of("舒适鞋", "背包", "水壶", "地图", "雨伞"));
        ROUTE_TYPE_EQUIPMENTS.put("露营", List.of("帐篷", "睡袋", "防潮垫", "露营灯", "炊具"));

        // 初始化天气对应的装备
        WEATHER_EQUIPMENTS.put("晴天", List.of("遮阳帽", "防晒霜", "太阳镜", "水壶"));
        WEATHER_EQUIPMENTS.put("雨天", List.of("雨衣", "防水鞋", "防水袋", "雨伞"));
        WEATHER_EQUIPMENTS.put("雪天", List.of("雪地靴", "保暖衣", "手套", "帽子", "防滑链"));
        WEATHER_EQUIPMENTS.put("多云", List.of("轻便外套", "水壶", "帽子"));
        WEATHER_EQUIPMENTS.put("高温", List.of("遮阳帽", "防晒霜", "水壶", "速干衣", "太阳镜"));
        WEATHER_EQUIPMENTS.put("低温", List.of("保暖衣", "手套", "帽子", "围巾", "保暖袜"));
    }

    public EquipmentChecklistServiceImpl(EquipmentChecklistMapper checklistMapper, EquipmentChecklistItemMapper checklistItemMapper) {
        this.checklistMapper = checklistMapper;
        this.checklistItemMapper = checklistItemMapper;
    }

    @Override
    @Transactional
    public EquipmentChecklist createSmartChecklist(Long userId, String name, String routeType, String weather) {
        // 创建清单
        EquipmentChecklist checklist = new EquipmentChecklist();
        checklist.setUserId(userId);
        checklist.setName(name);
        checklist.setRouteType(routeType);
        checklist.setWeather(weather);
        checklist.setCreateTime(LocalDateTime.now());
        checklistMapper.insert(checklist);

        // 生成推荐装备列表
        List<String> recommendedEquipments = new ArrayList<>();
        
        // 添加路线类型对应的装备
        if (ROUTE_TYPE_EQUIPMENTS.containsKey(routeType)) {
            recommendedEquipments.addAll(ROUTE_TYPE_EQUIPMENTS.get(routeType));
        }
        
        // 添加天气对应的装备
        if (WEATHER_EQUIPMENTS.containsKey(weather)) {
            recommendedEquipments.addAll(WEATHER_EQUIPMENTS.get(weather));
        }

        // 去重并创建清单项目
        List<String> uniqueEquipments = new ArrayList<>();
        for (String equipment : recommendedEquipments) {
            if (!uniqueEquipments.contains(equipment)) {
                uniqueEquipments.add(equipment);
                addChecklistItem(checklist.getId(), null, equipment, 1);
            }
        }

        return checklist;
    }

    @Override
    public EquipmentChecklist getChecklistById(Long id) {
        return checklistMapper.findById(id);
    }

    @Override
    public List<EquipmentChecklist> getChecklistsByUserId(Long userId) {
        return checklistMapper.findByUserId(userId);
    }

    @Override
    public boolean updateChecklistName(Long id, String name) {
        return checklistMapper.updateName(id, name) > 0;
    }

    @Override
    @Transactional
    public boolean deleteChecklist(Long id) {
        // 先删除所有清单项目
        checklistItemMapper.deleteByChecklistId(id);
        // 再删除清单
        return checklistMapper.deleteById(id) > 0;
    }

    @Override
    public EquipmentChecklistItem addChecklistItem(Long checklistId, Long equipmentId, String customName, Integer quantity) {
        EquipmentChecklistItem item = new EquipmentChecklistItem();
        item.setChecklistId(checklistId);
        item.setEquipmentId(equipmentId);
        item.setItemName(customName);
        item.setCustomName(customName); // 设置自定义名称，用于前端显示
        item.setItemType("system"); // 设置物品类型为系统推荐
        item.setQuantity(quantity);
        item.setPacked(false);
        item.setRental(false); // 默认不是租赁装备

        checklistItemMapper.insert(item);
        return item;
    }

    @Override
    public boolean updateItemPackedStatus(Long itemId, Boolean isPacked) {
        return checklistItemMapper.updatePackedStatus(itemId, isPacked) > 0;
    }

    @Override
    public boolean updateItemQuantity(Long itemId, Integer quantity) {
        return checklistItemMapper.updateQuantity(itemId, quantity) > 0;
    }

    @Override
    public boolean deleteChecklistItem(Long itemId) {
        return checklistItemMapper.deleteById(itemId) > 0;
    }

    @Override
    public List<EquipmentChecklistItem> getChecklistItems(Long checklistId) {
        return checklistItemMapper.findByChecklistId(checklistId);
    }
}
