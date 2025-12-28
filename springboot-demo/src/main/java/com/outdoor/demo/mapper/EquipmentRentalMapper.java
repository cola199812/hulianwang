package com.outdoor.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.outdoor.demo.entity.EquipmentRental;

@Mapper
/**
 * 装备租赁数据访问接口
 * 负责EquipmentRental表的数据库操作
 */
public interface EquipmentRentalMapper {
    /**
     * 创建租赁记录
     * @param rental 租赁记录对象
     * @return 影响行数
     */
    @Insert("INSERT INTO equipment_rental(user_id, equipment_id, start_time, end_time, rental_days, daily_rental_price, total_price, deposit, payment_status, rental_status, contact_phone) VALUES(#{userId}, #{equipmentId}, #{startTime}, #{endTime}, #{rentalDays}, #{dailyRentalPrice}, #{totalPrice}, #{deposit}, #{paymentStatus}, #{rentalStatus}, #{contactPhone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EquipmentRental rental);

    /**
     * 根据ID查询租赁记录
     * @param id 租赁记录ID
     * @return 租赁记录对象
     */
    @Select("SELECT * FROM equipment_rental WHERE id = #{id}")
    EquipmentRental findById(Long id);

    /**
     * 根据用户ID查询租赁记录
     * @param userId 用户ID
     * @return 租赁记录列表
     */
    @Select("SELECT * FROM equipment_rental WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<EquipmentRental> findByUserId(Long userId);

    /**
     * 更新支付状态
     * @param id 租赁记录ID
     * @param paymentStatus 支付状态
     * @return 影响行数
     */
    @Update("UPDATE equipment_rental SET payment_status = #{paymentStatus} WHERE id = #{id}")
    int updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") String paymentStatus);

    /**
     * 更新租赁状态
     * @param id 租赁记录ID
     * @param rentalStatus 租赁状态
     * @return 影响行数
     */
    @Update("UPDATE equipment_rental SET rental_status = #{rentalStatus} WHERE id = #{id}")
    int updateRentalStatus(@Param("id") Long id, @Param("rentalStatus") String rentalStatus);
}
