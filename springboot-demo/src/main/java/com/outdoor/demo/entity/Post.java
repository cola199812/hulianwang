package com.outdoor.demo.entity;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String markdown;
    private String locationName;
    private Double lat;
    private Double lng;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private String coverUrl;
    private LocalDateTime createTime;
    
    // 非数据库字段，用于返回距离
    private Double distance;
    
    // 关联的图片和视频
    private java.util.List<PostImage> images;
    private PostVideo video;
    
    // 关联的话题
    private java.util.List<Topic> topics;

    // 当前用户是否已点赞
    @org.springframework.data.annotation.Transient
    private Boolean isLiked;

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
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public java.util.List<PostImage> getImages() { return images; }
    public void setImages(java.util.List<PostImage> images) { this.images = images; }
    
    public PostVideo getVideo() { return video; }
    public void setVideo(PostVideo video) { this.video = video; }
    
    public java.util.List<Topic> getTopics() { return topics; }
    public void setTopics(java.util.List<Topic> topics) { this.topics = topics; }

    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
}
