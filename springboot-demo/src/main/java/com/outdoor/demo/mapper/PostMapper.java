package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    int insert(Post post);
    List<Post> findRecent();
    List<Post> findByUserId(@Param("userId") Long userId);
    Post findById(@Param("id") Long id);
}
