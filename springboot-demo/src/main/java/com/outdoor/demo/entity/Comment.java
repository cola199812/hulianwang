package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 评论实体类
 * 对应数据库中的comment表，存储用户对帖子的评论信息。
 */
public class Comment {
    /** 评论ID */
    private Long id;
    /** 关联的帖子ID */
    private Long postId;
    /** 评论者用户ID */
    private Long userId;
    /** 父评论ID（用于回复） */
    private Long parentId;
    /** 评论内容 */
    private String content;
    /** 创建时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
