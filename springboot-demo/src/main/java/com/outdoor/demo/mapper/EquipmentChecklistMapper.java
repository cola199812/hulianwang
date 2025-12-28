package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.EquipmentChecklist;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
/**
 * 装备清单数据访问接口
 * 负责EquipmentChecklist表的数据库操作
 */
public interface EquipmentChecklistMapper {
    /**
     * 创建装备清单
     * @param checklist 装备清单对象
     * @return 影响行数
     */
    @Insert("INSERT INTO equipment_checklist(user_id, name, route_type, weather, create_time) VALUES(#{userId}, #{name}, #{routeType}, #{weather}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EquipmentChecklist checklist);

    /**
     * 根据ID查询装备清单
     * @param id 装备清单ID
     * @return 装备清单对象
     */
    @Select("SELECT * FROM equipment_checklist WHERE id = #{id}")
    EquipmentChecklist findById(Long id);

    /**
     * 根据用户ID查询装备清单列表
     * @param userId 用户ID
     * @return 装备清单列表
     */
    @Select("SELECT * FROM equipment_checklist WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<EquipmentChecklist> findByUserId(Long userId);

    /**
     * 更新装备清单名称
     * @param id 装备清单ID
     * @param name 新名称
     * @return 影响行数
     */
    @Update("UPDATE equipment_checklist SET name = #{name} WHERE id = #{id}")
    int updateName(@Param("id") Long id, @Param("name") String name);

    /**
     * 删除装备清单
     * @param id 装备清单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM equipment_checklist WHERE id = #{id}")
    int deleteById(Long id);
}
