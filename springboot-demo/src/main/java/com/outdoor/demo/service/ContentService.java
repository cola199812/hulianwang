package com.outdoor.demo.service;

import com.outdoor.demo.entity.Post;
import com.outdoor.demo.entity.Media;
import com.outdoor.demo.entity.PostImage;
import com.outdoor.demo.entity.PostVideo;

import java.util.List;

public interface ContentService {
    Long createPost(Post post);
    List<Post> listRecentPosts();
    List<Post> listUserPosts(Long userId);
    List<Post> listNearbyPosts(Double lat, Double lng, Double radius);
    List<Media> listUserMedia(Long userId);
    Long saveMedia(Media media);
    List<Media> listMediaByPost(Long postId);
    
    // New methods for PostImage and PostVideo
    void savePostImages(List<PostImage> images);
    void savePostVideo(PostVideo video);
    List<PostImage> listPostImages(Long postId);
    PostVideo getPostVideo(Long postId);

    Post getPost(Long id);
    int countPostLikes(Long postId);
    boolean togglePostLike(Long postId, Long userId);
    Long addComment(com.outdoor.demo.entity.Comment comment);
    java.util.List<com.outdoor.demo.entity.Comment> listCommentsByPost(Long postId);
    int countComments(Long postId);
    int countCommentLikes(Long commentId);
    boolean toggleCommentLike(Long commentId, Long userId);
    boolean deletePost(Long postId, Long userId);
}
