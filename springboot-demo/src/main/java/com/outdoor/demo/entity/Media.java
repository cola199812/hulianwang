package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 媒体资源实体类
 * 对应数据库中的media表，存储图片、视频等媒体资源信息。
 */
public class Media {
    /** 媒体资源ID */
    private Long id;
    /** 上传者用户ID */
    private Long userId;
    /** 关联的帖子ID */
    private Long postId;
    /** 媒体类型（如：image, video） */
    private String type;
    /** 资源URL地址 */
    private String url;
    /** 缩略图URL地址 */
    private String thumbnailUrl;
    /** 所属相册 */
    private String album;
    /** 标签（逗号分隔） */
    private String tags;
    /** 创建/上传时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
