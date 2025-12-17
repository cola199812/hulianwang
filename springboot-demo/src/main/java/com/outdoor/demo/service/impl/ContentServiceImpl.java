package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Post;
import com.outdoor.demo.entity.Media;
import com.outdoor.demo.mapper.PostMapper;
import com.outdoor.demo.mapper.MediaMapper;
import com.outdoor.demo.service.ContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    private final PostMapper postMapper;
    private final MediaMapper mediaMapper;

    public ContentServiceImpl(PostMapper postMapper, MediaMapper mediaMapper) {
        this.postMapper = postMapper;
        this.mediaMapper = mediaMapper;
    }

    @Override
    @Transactional
    public Long createPost(Post post) {
        try {
            postMapper.insert(post);
            return post.getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Post> listRecentPosts() {
        try {
            return postMapper.findRecent();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Post> listUserPosts(Long userId) {
        try {
            return postMapper.findByUserId(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Media> listUserMedia(Long userId) {
        try {
            return mediaMapper.findByUserId(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public Long saveMedia(Media media) {
        try {
            mediaMapper.insert(media);
            return media.getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Media> listMediaByPost(Long postId) {
        try {
            return mediaMapper.findByPostId(postId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Post getPost(Long id) {
        try {
            return postMapper.findById(id);
        } catch (Exception e) {
            return null;
        }
    }
}
