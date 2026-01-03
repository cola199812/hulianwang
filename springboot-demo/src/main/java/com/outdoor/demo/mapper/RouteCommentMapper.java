package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.RouteComment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface RouteCommentMapper {

    @Insert("INSERT INTO route_comment (route_id, user_id, user_name, user_avatar, content, rating, create_time, like_count) VALUES (#{routeId}, #{userId}, #{userName}, #{userAvatar}, #{content}, #{rating}, NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RouteComment comment);

    @Select("SELECT * FROM route_comment WHERE route_id = #{routeId} ORDER BY create_time DESC")
    List<RouteComment> findByRouteId(@Param("routeId") Long routeId);

    @Select("SELECT COUNT(*) FROM route_comment WHERE route_id = #{routeId}")
    int countByRouteId(@Param("routeId") Long routeId);

    @Update("UPDATE route_comment SET like_count = like_count + 1 WHERE id = #{commentId}")
    int incrementLikeCount(@Param("commentId") Long commentId);

    @Select("SELECT rating, COUNT(*) as count FROM route_comment WHERE route_id = #{routeId} GROUP BY rating")
    List<Map<String, Object>> getRatingStats(@Param("routeId") Long routeId);
}