package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Media;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MediaMapper {
    int insert(Media media);
    List<Media> findByUserId(@Param("userId") Long userId);
    List<Media> findByPostId(@Param("postId") Long postId);
    int deleteByPostId(@Param("postId") Long postId);
}
