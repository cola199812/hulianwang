package com.outdoor.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.outdoor.demo.entity.EquipmentChecklistItem;

@Mapper
/**
 * 装备清单项目数据访问接口
 * 负责EquipmentChecklistItem表的数据库操作
 */
public interface EquipmentChecklistItemMapper {
    /**
     * 创建清单项目
     * @param item 清单项目对象
     * @return 影响行数
     */
    @Insert("INSERT INTO equipment_checklist_item(checklist_id, equipment_id, item_name, item_type, quantity, is_packed) VALUES(#{checklistId}, #{equipmentId}, #{itemName}, #{itemType}, #{quantity}, #{isPacked})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EquipmentChecklistItem item);

    /**
     * 根据清单ID查询所有项目
     * @param checklistId 清单ID
     * @return 清单项目列表
     */
    @Select("SELECT * FROM equipment_checklist_item WHERE checklist_id = #{checklistId}")
    List<EquipmentChecklistItem> findByChecklistId(Long checklistId);

    /**
     * 更新项目打包状态
     * @param id 项目ID
     * @param isPacked 是否已打包
     * @return 影响行数
     */
    @Update("UPDATE equipment_checklist_item SET is_packed = #{isPacked} WHERE id = #{id}")
    int updatePackedStatus(@Param("id") Long id, @Param("isPacked") Boolean isPacked);

    /**
     * 更新项目数量
     * @param id 项目ID
     * @param quantity 新数量
     * @return 影响行数
     */
    @Update("UPDATE equipment_checklist_item SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 删除清单项目
     * @param id 项目ID
     * @return 影响行数
     */
    @Delete("DELETE FROM equipment_checklist_item WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 删除清单下所有项目
     * @param checklistId 清单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM equipment_checklist_item WHERE checklist_id = #{checklistId}")
    int deleteByChecklistId(Long checklistId);
}
