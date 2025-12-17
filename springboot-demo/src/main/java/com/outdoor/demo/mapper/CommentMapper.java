package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    int insert(Comment comment);
    List<Comment> findByPostId(@Param("postId") Long postId);
    int countByPostId(@Param("postId") Long postId);
    int deleteByPostId(@Param("postId") Long postId);
}
