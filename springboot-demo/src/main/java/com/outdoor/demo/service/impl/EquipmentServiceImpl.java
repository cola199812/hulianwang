package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Equipment;
import com.outdoor.demo.mapper.EquipmentMapper;
import com.outdoor.demo.service.EquipmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * 装备服务实现类
 * 实现装备租赁相关的业务逻辑
 */
public class EquipmentServiceImpl implements EquipmentService {
    private final EquipmentMapper equipmentMapper;

    public EquipmentServiceImpl(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    public List<Equipment> getAllAvailableEquipments() {
        return equipmentMapper.findAllAvailable();
    }

    @Override
    public List<Equipment> getEquipmentsByType(String type) {
        return equipmentMapper.findByType(type);
    }

    @Override
    public Equipment getEquipmentById(Long id) {
        return equipmentMapper.findById(id);
    }

    @Override
    public boolean decreaseStock(Long id, Integer quantity) {
        int result = equipmentMapper.decreaseStock(id, quantity);
        return result > 0;
    }

    @Override
    public boolean increaseStock(Long id, Integer quantity) {
        int result = equipmentMapper.increaseStock(id, quantity);
        return result > 0;
    }
}
