package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 帖子实体类
 * 对应数据库中的post表，存储用户发布的帖子内容。
 */
public class Post {
    /** 帖子ID */
    private Long id;
    /** 发帖人用户ID */
    private Long userId;
    /** 标题 */
    private String title;
    /** Markdown内容 */
    private String markdown;
    /** 地点名称 */
    private String locationName;
    /** 纬度 */
    private Double lat;
    /** 经度 */
    private Double lng;
    /** 封面图片URL */
    private String coverUrl;
    /** 发布时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMarkdown() { return markdown; }
    public void setMarkdown(String markdown) { this.markdown = markdown; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
