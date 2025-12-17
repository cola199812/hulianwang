package com.outdoor.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentLikeMapper {
    int countByCommentId(@Param("commentId") Long commentId);
    Integer exists(@Param("commentId") Long commentId, @Param("userId") Long userId);
    int insert(@Param("commentId") Long commentId, @Param("userId") Long userId);
    int delete(@Param("commentId") Long commentId, @Param("userId") Long userId);
    int deleteByPostId(@Param("postId") Long postId);
}
