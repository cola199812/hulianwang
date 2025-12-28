package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 帖子数据访问接口
 * 负责Post表的数据库操作。
 */
public interface PostMapper {
    /**
     * 插入新帖子
     * @param post 帖子对象
     * @return 影响行数
     */
    int insert(Post post);

    /**
     * 查询最新帖子列表
     * @return 帖子列表
     */
    List<Post> findRecent();

    /**
     * 查询指定用户的帖子
     * @param userId 用户ID
     * @return 帖子列表
     */
    List<Post> findByUserId(@Param("userId") Long userId);

    /**
     * 根据ID查询帖子详情
     * @param id 帖子ID
     * @return 帖子对象
     */
    Post findById(@Param("id") Long id);

    /**
     * 删除帖子
     * @param id 帖子ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
