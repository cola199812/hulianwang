package com.outdoor.demo.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outdoor.demo.entity.Equipment;
import com.outdoor.demo.entity.EquipmentRental;
import com.outdoor.demo.entity.EquipmentRentalRequest;
import com.outdoor.demo.mapper.EquipmentRentalMapper;
import com.outdoor.demo.service.EquipmentRentalService;
import com.outdoor.demo.service.EquipmentService;
import com.outdoor.demo.service.PersonalEquipmentService;

@Service
/**
 * 装备租赁服务实现类
 * 实现装备租赁记录相关的业务逻辑
 */
public class EquipmentRentalServiceImpl implements EquipmentRentalService {
    private final EquipmentRentalMapper rentalMapper;
    private final EquipmentService equipmentService;
    private final PersonalEquipmentService personalEquipmentService;

    public EquipmentRentalServiceImpl(EquipmentRentalMapper rentalMapper, EquipmentService equipmentService, PersonalEquipmentService personalEquipmentService) {
        this.rentalMapper = rentalMapper;
        this.equipmentService = equipmentService;
        this.personalEquipmentService = personalEquipmentService;
    }

    @Override
    @Transactional
    public EquipmentRental createRental(EquipmentRentalRequest request, Long userId) {
        // 获取装备信息
        Equipment equipment = equipmentService.getEquipmentById(request.getEquipmentId());
        if (equipment == null) {
            throw new RuntimeException("装备不存在");
        }

        // 计算租赁天数
        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
        long days = ChronoUnit.DAYS.between(startTime, endTime) + 1;
        if (days <= 0) {
            throw new RuntimeException("租赁时间无效");
        }

        // 计算总价和押金
        double totalPrice = equipment.getDailyRentalPrice() * days;
        double deposit = equipment.getDeposit();

        // 减少装备库存
        if (!equipmentService.decreaseStock(request.getEquipmentId(), 1)) {
            throw new RuntimeException("装备库存不足");
        }

        // 创建租赁记录
        EquipmentRental rental = new EquipmentRental();
        rental.setUserId(userId);
        rental.setEquipmentId(request.getEquipmentId());
        rental.setStartTime(startTime);
        rental.setEndTime(endTime);
        rental.setRentalDays((int) days);
        rental.setDailyRentalPrice(equipment.getDailyRentalPrice());
        rental.setTotalPrice(totalPrice);
        rental.setDeposit(deposit);
        rental.setPaymentStatus("PENDING");
        rental.setRentalStatus("RESERVED");
        rental.setContactPhone(request.getContactPhone());
        rental.setCreateTime(LocalDateTime.now());

        rentalMapper.insert(rental);
        
        // 将租赁的装备添加到个人装备库
        personalEquipmentService.addPersonalEquipment(
            userId, 
            equipment.getName(), 
            equipment.getType(), 
            null, // 购买日期为空（租赁装备）
            "租赁装备 - 租赁期: " + startTime.toLocalDate() + " 至 " + endTime.toLocalDate()
        );
        
        return rental;
    }

    @Override
    public EquipmentRental getRentalById(Long id) {
        return rentalMapper.findById(id);
    }

    @Override
    public List<EquipmentRental> getRentalsByUserId(Long userId) {
        return rentalMapper.findByUserId(userId);
    }

    @Override
    public boolean completePayment(Long rentalId) {
        return rentalMapper.updatePaymentStatus(rentalId, "PAID") > 0;
    }

    @Override
    public boolean updateRentalStatus(Long rentalId, String status) {
        return rentalMapper.updateRentalStatus(rentalId, status) > 0;
    }
}
