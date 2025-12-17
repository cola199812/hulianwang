package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityMapper {
    int insert(Activity activity);
    Activity findById(@Param("id") Long id);
    List<Activity> findAll();
}

