package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.PostImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostImageMapper {
    int insert(PostImage postImage);
    List<PostImage> findByPostId(@Param("postId") Long postId);
}
