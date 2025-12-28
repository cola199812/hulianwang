package com.outdoor.demo.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.outdoor.demo.entity.PersonalEquipment;
import com.outdoor.demo.mapper.PersonalEquipmentMapper;
import com.outdoor.demo.service.PersonalEquipmentService;

@Service
/**
 * 个人装备服务实现类
 * 实现个人装备管理相关的业务逻辑
 */
public class PersonalEquipmentServiceImpl implements PersonalEquipmentService {
    private final PersonalEquipmentMapper personalEquipmentMapper;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PersonalEquipmentServiceImpl(PersonalEquipmentMapper personalEquipmentMapper) {
        this.personalEquipmentMapper = personalEquipmentMapper;
    }

    @Override
    public PersonalEquipment addPersonalEquipment(Long userId, String name, String type, String purchaseDate, String note) {
        PersonalEquipment equipment = new PersonalEquipment();
        equipment.setUserId(userId);
        equipment.setName(name);
        equipment.setType(type);
        equipment.setUsageCount(0);
        equipment.setNote(note);
        equipment.setStatus("良好"); // 设置默认状态为良好

        // 设置默认的下次维护日期（购买后3个月）
        if (purchaseDate != null) {
            LocalDate purchase = LocalDate.parse(purchaseDate, dateFormatter);
            LocalDate nextMaintenance = purchase.plusMonths(3);
            equipment.setPurchaseDate(purchase.atStartOfDay());
            equipment.setNextMaintenanceDate(nextMaintenance.atStartOfDay());
        }

        personalEquipmentMapper.insert(equipment);
        return equipment;
    }

    @Override
    public PersonalEquipment getPersonalEquipmentById(Long id) {
        return personalEquipmentMapper.findById(id);
    }

    @Override
    public List<PersonalEquipment> getPersonalEquipmentsByUserId(Long userId) {
        return personalEquipmentMapper.findByUserId(userId);
    }

    @Override
    public List<PersonalEquipment> getPersonalEquipmentsByType(Long userId, String type) {
        return personalEquipmentMapper.findByUserIdAndType(userId, type);
    }

    @Override
    public boolean increaseUsageCount(Long id) {
        PersonalEquipment equipment = personalEquipmentMapper.findById(id);
        if (equipment != null) {
            int newCount = equipment.getUsageCount() + 1;
            return personalEquipmentMapper.updateUsageCount(id, newCount) > 0;
        }
        return false;
    }

    @Override
    public boolean updateNextMaintenanceDate(Long id, String nextMaintenanceDate) {
        return personalEquipmentMapper.updateNextMaintenanceDate(id, nextMaintenanceDate) > 0;
    }

    @Override
    public boolean updatePersonalEquipment(Long id, String name, String type) {
        PersonalEquipment equipment = personalEquipmentMapper.findById(id);
        if (equipment != null) {
            equipment.setName(name);
            equipment.setType(type);
            return personalEquipmentMapper.update(equipment) > 0;
        }
        return false;
    }

    @Override
    public boolean deletePersonalEquipment(Long id) {
        return personalEquipmentMapper.deleteById(id) > 0;
    }

    @Override
    public List<PersonalEquipment> getEquipmentsNeedingMaintenance(Long userId) {
        List<PersonalEquipment> allEquipments = personalEquipmentMapper.findByUserId(userId);
        LocalDate today = LocalDate.now();

        return allEquipments.stream()
                .filter(equipment -> equipment.getNextMaintenanceDate() != null)
                .filter(equipment -> {
                    LocalDate maintenanceDate = equipment.getNextMaintenanceDate().toLocalDate();
                    return maintenanceDate.isBefore(today) || maintenanceDate.isEqual(today);
                })
                .collect(Collectors.toList());
    }
}
