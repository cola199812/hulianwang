package com.outdoor.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.outdoor.demo.entity.PersonalEquipment;

@Mapper
/**
 * 个人装备数据访问接口
 * 负责PersonalEquipment表的数据库操作
 */
public interface PersonalEquipmentMapper {
    /**
     * 创建个人装备记录
     * @param equipment 个人装备对象
     * @return 影响行数
     */
    @Insert("INSERT INTO personal_equipment(user_id, name, type, purchase_date, usage_count, next_maintenance_date, status, note) VALUES(#{userId}, #{name}, #{type}, #{purchaseDate}, #{usageCount}, #{nextMaintenanceDate}, #{status}, #{note})")
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
     * 根据用户ID查询个人装备列表
     * @param userId 用户ID
     * @return 个人装备列表
     */
    @Select("SELECT * FROM personal_equipment WHERE user_id = #{userId}")
    List<PersonalEquipment> findByUserId(Long userId);

    /**
     * 根据类型查询个人装备
     * @param userId 用户ID
     * @param type 装备类型
     * @return 个人装备列表
     */
    @Select("SELECT * FROM personal_equipment WHERE user_id = #{userId} AND type = #{type}")
    List<PersonalEquipment> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 更新使用次数
     * @param id 装备ID
     * @param usageCount 使用次数
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
     * 更新装备信息
     * @param equipment 装备对象
     * @return 影响行数
     */
    @Update("UPDATE personal_equipment SET name = #{name}, type = #{type} WHERE id = #{id}")
    int update(PersonalEquipment equipment);

    /**
     * 删除个人装备
     * @param id 装备ID
     * @return 影响行数
     */
    @Delete("DELETE FROM personal_equipment WHERE id = #{id}")
    int deleteById(Long id);
}
