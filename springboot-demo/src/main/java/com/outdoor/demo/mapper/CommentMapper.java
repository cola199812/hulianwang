package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 评论数据访问接口
 * 负责Comment表的数据库操作。
 */
public interface CommentMapper {
    /**
     * 插入新评论
     * @param comment 评论对象
     * @return 影响行数
     */
    int insert(Comment comment);

    /**
     * 查询指定帖子的所有评论
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<Comment> findByPostId(@Param("postId") Long postId);

    /**
     * 统计指定帖子的评论数
     * @param postId 帖子ID
     * @return 评论数
     */
    int countByPostId(@Param("postId") Long postId);

    /**
     * 删除指定帖子的所有评论
     * @param postId 帖子ID
     * @return 影响行数
     */
    int deleteByPostId(@Param("postId") Long postId);
}
