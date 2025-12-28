package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 评论点赞实体类
 * 对应数据库中的comment_like表，记录用户对评论的点赞信息。
 */
public class CommentLike {
    /** 记录ID */
    private Long id;
    /** 评论ID */
    private Long commentId;
    /** 点赞用户ID */
    private Long userId;
    /** 点赞时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
