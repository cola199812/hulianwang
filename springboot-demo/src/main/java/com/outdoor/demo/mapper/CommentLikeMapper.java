package com.outdoor.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
/**
 * 评论点赞数据访问接口
 * 负责CommentLike表的数据库操作。
 */
public interface CommentLikeMapper {
    /**
     * 统计评论点赞数
     * @param commentId 评论ID
     * @return 点赞数
     */
    int countByCommentId(@Param("commentId") Long commentId);

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 1表示存在，null或0表示不存在
     */
    Integer exists(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 添加点赞记录
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int insert(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 取消点赞
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int delete(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 删除指定帖子下所有评论的点赞记录
     * @param postId 帖子ID
     * @return 影响行数
     */
    int deleteByPostId(@Param("postId") Long postId);
}
