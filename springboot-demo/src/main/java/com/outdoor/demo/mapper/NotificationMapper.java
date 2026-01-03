package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 通知Mapper接口
 * 用于通知相关的数据库操作
 */
@Mapper
public interface NotificationMapper {

    /**
     * 插入通知
     */
    @Insert("INSERT INTO notification(user_id, title, content, type, is_read) VALUES(#{userId}, #{title}, #{content}, #{type}, #{isRead})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    /**
     * 根据用户ID获取通知列表
     */
    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Notification> getByUserId(Long userId);

    /**
     * 根据用户ID和类型获取通知列表
     */
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND type = #{type} ORDER BY create_time DESC")
    List<Notification> getByUserIdAndType(Long userId, String type);

    /**
     * 标记通知为已读
     */
    @Update("UPDATE notification SET is_read = true WHERE id = #{id} AND user_id = #{userId}")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 标记所有通知为已读
     */
    @Update("UPDATE notification SET is_read = true WHERE user_id = #{userId}")
    int markAllAsRead(Long userId);

    /**
     * 删除通知
     */
    @Delete("DELETE FROM notification WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 获取未读通知数量
     */
    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = false")
    int getUnreadCount(Long userId);
}