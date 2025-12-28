package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Equipment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
/**
 * 装备数据访问接口
 * 负责Equipment表的数据库操作
 */
public interface EquipmentMapper {
    /**
     * 查询所有可租赁装备
     * @return 装备列表
     */
    @Select("SELECT * FROM equipment WHERE stock > 0")
    List<Equipment> findAllAvailable();

    /**
     * 根据ID查询装备
     * @param id 装备ID
     * @return 装备对象
     */
    @Select("SELECT * FROM equipment WHERE id = #{id}")
    Equipment findById(Long id);

    /**
     * 根据类型查询装备
     * @param type 装备类型
     * @return 装备列表
     */
    @Select("SELECT * FROM equipment WHERE type = #{type} AND stock > 0")
    List<Equipment> findByType(String type);

    /**
     * 减少装备库存
     * @param id 装备ID
     * @param quantity 减少数量
     * @return 影响行数
     */
    @Update("UPDATE equipment SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 增加装备库存
     * @param id 装备ID
     * @param quantity 增加数量
     * @return 影响行数
     */
    @Update("UPDATE equipment SET stock = stock + #{quantity} WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
