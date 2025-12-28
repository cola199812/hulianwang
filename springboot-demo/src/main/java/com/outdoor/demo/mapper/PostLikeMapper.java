package com.outdoor.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
/**
 * 帖子点赞数据访问接口
 * 负责PostLike表的数据库操作。
 */
public interface PostLikeMapper {
    /**
     * 统计帖子点赞数
     * @param postId 帖子ID
     * @return 点赞数
     */
    int countByPostId(@Param("postId") Long postId);

    /**
     * 检查用户是否已点赞帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 1表示存在，null或0表示不存在
     */
    Integer exists(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 添加点赞记录
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int insert(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 取消点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int delete(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 删除指定帖子的所有点赞记录
     * @param postId 帖子ID
     * @return 影响行数
     */
    int deleteByPostId(@Param("postId") Long postId);
}
