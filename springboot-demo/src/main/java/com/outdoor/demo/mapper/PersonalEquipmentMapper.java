package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.PersonalEquipment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
/**
 * 个人装备数据访问接口
 * 负责PersonalEquipment表的数据库操作
 */
public interface PersonalEquipmentMapper {
    /**
     * 添加个人装备
     * @param equipment 个人装备对象
     * @return 影响行数
     */
    @Insert("INSERT INTO personal_equipment(user_id, name, type, brand, purchase_date, purchase_price, usage_count, last_usage_date, next_maintenance_date, status, image_url, note) " +
            "VALUES(#{userId}, #{name}, #{type}, #{brand}, #{purchaseDate}, #{purchasePrice}, #{usageCount}, #{lastUsageDate}, #{nextMaintenanceDate}, #{status}, #{imageUrl}, #{note})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PersonalEquipment equipment);

    /**
     * 根据ID查询个人装备
     * @param id 装备ID
     * @return 个人装备对象
     */
    @Select("SELECT * FROM personal_equipment WHERE id = #{id}")
    PersonalEquipment findById(Long id);

    /**
     * 根据用户ID查询所有个人装备
     * @param userId 用户ID
     * @return 个人装备列表
     */
    @Select("SELECT * FROM personal_equipment WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PersonalEquipment> findByUserId(Long userId);

    /**
     * 根据用户ID和类型查询个人装备
     * @param userId 用户ID
     * @param type 装备类型
     * @return 个人装备列表
     */
    @Select("SELECT * FROM personal_equipment WHERE user_id = #{userId} AND type = #{type} ORDER BY create_time DESC")
    List<PersonalEquipment> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 更新使用次数
     * @param id 装备ID
     * @param usageCount 新的使用次数
     * @return 影响行数
     */
    @Update("UPDATE personal_equipment SET usage_count = #{usageCount} WHERE id = #{id}")
    int updateUsageCount(@Param("id") Long id, @Param("usageCount") Integer usageCount);

    /**
     * 更新下次维护日期
     * @param id 装备ID
     * @param nextMaintenanceDate 下次维护日期
     * @return 影响行数
     */
    @Update("UPDATE personal_equipment SET next_maintenance_date = #{nextMaintenanceDate} WHERE id = #{id}")
    int updateNextMaintenanceDate(@Param("id") Long id, @Param("nextMaintenanceDate") String nextMaintenanceDate);

    /**
     * 更新个人装备信息
     * @param equipment 个人装备对象
     * @return 影响行数
     */
    @Update("UPDATE personal_equipment SET name = #{name}, type = #{type}, brand = #{brand}, " +
            "purchase_date = #{purchaseDate}, purchase_price = #{purchasePrice}, " +
            "usage_count = #{usageCount}, last_usage_date = #{lastUsageDate}, " +
            "next_maintenance_date = #{nextMaintenanceDate}, status = #{status}, " +
            "image_url = #{imageUrl}, note = #{note} WHERE id = #{id}")
    int update(PersonalEquipment equipment);

    /**
     * 删除个人装备
     * @param id 装备ID
     * @return 影响行数
     */
    @Delete("DELETE FROM personal_equipment WHERE id = #{id}")
    int deleteById(Long id);
}
