package com.outdoor.demo.entity;

public class PostImage {
    private Long id;
    private Long postId;
    private String imageUrl;
    private String description;
    private Integer sortOrder;
    private String tag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
