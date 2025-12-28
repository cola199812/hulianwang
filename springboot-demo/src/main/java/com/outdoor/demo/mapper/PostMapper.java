package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostMapper {
    int insert(Post post);
    List<Post> findRecent();
    List<Post> findByUserId(@Param("userId") Long userId);
    Post findById(@Param("id") Long id);
    int deleteById(@Param("id") Long id);

    /**
     * 查询附近动态
     * 使用 ST_DISTANCE_SPHERE 计算球面距离 (单位: 米)
     * @param lat 当前纬度
     * @param lng 当前经度
     * @param radius 半径 (米)
     */
    @Select("SELECT *, ST_DISTANCE_SPHERE(POINT(lng, lat), POINT(#{lng}, #{lat})) as distance " +
            "FROM post " +
            "WHERE ST_DISTANCE_SPHERE(POINT(lng, lat), POINT(#{lng}, #{lat})) < #{radius} " +
            "ORDER BY distance ASC")
    List<Post> findNearby(@Param("lat") Double lat, @Param("lng") Double lng, @Param("radius") Double radius);
}
