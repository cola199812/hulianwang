package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Route;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RouteMapper {
    int insert(Route route);
    Route findById(@Param("id") Long id);
    List<Route> findAll();
}

