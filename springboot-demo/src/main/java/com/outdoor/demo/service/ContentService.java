package com.outdoor.demo.service;

import com.outdoor.demo.entity.Post;
import com.outdoor.demo.entity.Media;

import java.util.List;

public interface ContentService {
    Long createPost(Post post);
    List<Post> listRecentPosts();
    List<Post> listUserPosts(Long userId);
    List<Media> listUserMedia(Long userId);
    Long saveMedia(Media media);
    List<Media> listMediaByPost(Long postId);
    Post getPost(Long id);
}
