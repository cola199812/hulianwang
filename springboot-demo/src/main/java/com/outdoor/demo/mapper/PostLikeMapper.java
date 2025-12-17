package com.outdoor.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeMapper {
    int countByPostId(@Param("postId") Long postId);
    Integer exists(@Param("postId") Long postId, @Param("userId") Long userId);
    int insert(@Param("postId") Long postId, @Param("userId") Long userId);
    int delete(@Param("postId") Long postId, @Param("userId") Long userId);
    int deleteByPostId(@Param("postId") Long postId);
}
