package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    int insert(Notification notification);
    List<Notification> findByUserId(@Param("userId") Long userId);
    int markAsRead(@Param("id") Long id);
    int markAllAsRead(@Param("userId") Long userId);
    int delete(@Param("id") Long id);
    List<Notification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
}