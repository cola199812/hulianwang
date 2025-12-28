package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Media;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 媒体资源数据访问接口
 * 负责Media表的数据库操作，管理图片、视频等资源。
 */
public interface MediaMapper {
    /**
     * 插入媒体记录
     * @param media 媒体对象
     * @return 影响行数
     */
    int insert(Media media);

    /**
     * 查询用户的媒体资源
     * @param userId 用户ID
     * @return 媒体列表
     */
    List<Media> findByUserId(@Param("userId") Long userId);

    /**
     * 查询帖子的媒体资源
     * @param postId 帖子ID
     * @return 媒体列表
     */
    List<Media> findByPostId(@Param("postId") Long postId);

    /**
     * 删除指定帖子的所有媒体资源记录
     * @param postId 帖子ID
     * @return 影响行数
     */
    int deleteByPostId(@Param("postId") Long postId);
}
