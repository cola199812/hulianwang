package com.outdoor.demo.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface RouteFavoriteMapper {
    
    @Insert("INSERT INTO route_favorite (route_id, user_id, create_time) VALUES (#{routeId}, #{userId}, NOW())")
    int addFavorite(@Param("routeId") Long routeId, @Param("userId") Long userId);
    
    @Delete("DELETE FROM route_favorite WHERE route_id = #{routeId} AND user_id = #{userId}")
    int removeFavorite(@Param("routeId") Long routeId, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) > 0 FROM route_favorite WHERE route_id = #{routeId} AND user_id = #{userId}")
    boolean isFavorited(@Param("routeId") Long routeId, @Param("userId") Long userId);
}