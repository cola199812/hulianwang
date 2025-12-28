package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.PostVideo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostVideoMapper {
    int insert(PostVideo postVideo);
    List<PostVideo> findByPostId(@Param("postId") Long postId);
}
