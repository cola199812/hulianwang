package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 帖子点赞实体类
 * 对应数据库中的post_like表，记录用户对帖子的点赞信息。
 */
public class PostLike {
    /** 记录ID */
    private Long id;
    /** 帖子ID */
    private Long postId;
    /** 点赞用户ID */
    private Long userId;
    /** 点赞时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
