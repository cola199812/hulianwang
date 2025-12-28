package com.outdoor.demo.service;

import com.outdoor.demo.entity.Post;
import com.outdoor.demo.entity.Media;

import java.util.List;

/**
 * 内容服务接口
 * 定义帖子、评论、媒体等内容的业务逻辑方法。
 */
public interface ContentService {
    /**
     * 创建帖子
     * @param post 帖子对象
     * @return 帖子ID
     */
    Long createPost(Post post);

    /**
     * 获取最新帖子列表
     * @return 帖子列表
     */
    List<Post> listRecentPosts();

    /**
     * 获取指定用户的帖子列表
     * @param userId 用户ID
     * @return 帖子列表
     */
    List<Post> listUserPosts(Long userId);

    /**
     * 获取指定用户的媒体列表
     * @param userId 用户ID
     * @return 媒体列表
     */
    List<Media> listUserMedia(Long userId);

    /**
     * 保存媒体信息
     * @param media 媒体对象
     * @return 媒体ID
     */
    Long saveMedia(Media media);

    /**
     * 获取指定帖子的媒体列表
     * @param postId 帖子ID
     * @return 媒体列表
     */
    List<Media> listMediaByPost(Long postId);

    /**
     * 获取帖子详情
     * @param id 帖子ID
     * @return 帖子对象
     */
    Post getPost(Long id);

    /**
     * 统计帖子点赞数
     * @param postId 帖子ID
     * @return 点赞数
     */
    int countPostLikes(Long postId);

    /**
     * 切换帖子点赞状态（点赞/取消点赞）
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 当前是否已点赞
     */
    boolean togglePostLike(Long postId, Long userId);

    /**
     * 添加评论
     * @param comment 评论对象
     * @return 评论ID
     */
    Long addComment(com.outdoor.demo.entity.Comment comment);

    /**
     * 获取帖子的评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    java.util.List<com.outdoor.demo.entity.Comment> listCommentsByPost(Long postId);

    /**
     * 统计帖子评论数
     * @param postId 帖子ID
     * @return 评论数
     */
    int countComments(Long postId);

    /**
     * 统计评论点赞数
     * @param commentId 评论ID
     * @return 点赞数
     */
    int countCommentLikes(Long commentId);

    /**
     * 切换评论点赞状态
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 当前是否已点赞
     */
    boolean toggleCommentLike(Long commentId, Long userId);

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param userId 操作用户ID
     * @return 是否删除成功
     */
    boolean deletePost(Long postId, Long userId);
}
