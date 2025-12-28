package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Route;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 路线数据访问接口
 * 负责Route表的数据库操作。
 */
public interface RouteMapper {
    /**
     * 插入新路线
     * @param route 路线对象
     * @return 影响行数
     */
    int insert(Route route);

    /**
     * 根据ID查询路线
     * @param id 路线ID
     * @return 路线对象
     */
    Route findById(@Param("id") Long id);

    /**
     * 查询所有路线
     * @return 路线列表
     */
    List<Route> findAll();
}

