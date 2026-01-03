package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 通知实体类
 * 对应数据库中的notification表，存储用户通知信息。
 */
public class Notification {
    /** 通知ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 通知标题 */
    private String title;
    /** 通知内容 */
    private String content;
    /** 通知类型 */
    private String type;
    /** 是否已读 */
    private Boolean isRead;
    /** 创建时间 */
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}