package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Post;
import com.outdoor.demo.entity.Media;
import com.outdoor.demo.entity.Comment;
import com.outdoor.demo.mapper.PostMapper;
import com.outdoor.demo.mapper.MediaMapper;
import com.outdoor.demo.mapper.PostLikeMapper;
import com.outdoor.demo.mapper.CommentMapper;
import com.outdoor.demo.mapper.CommentLikeMapper;
import com.outdoor.demo.service.ContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
/**
 * 内容服务实现类
 * 实现帖子、评论、点赞等内容的业务逻辑。
 */
public class ContentServiceImpl implements ContentService {
    private final PostMapper postMapper;
    private final MediaMapper mediaMapper;
    private final PostLikeMapper postLikeMapper;
    private final CommentMapper commentMapper;
    private final CommentLikeMapper commentLikeMapper;

    public ContentServiceImpl(PostMapper postMapper, MediaMapper mediaMapper,
                              PostLikeMapper postLikeMapper, CommentMapper commentMapper, CommentLikeMapper commentLikeMapper) {
        this.postMapper = postMapper;
        this.mediaMapper = mediaMapper;
        this.postLikeMapper = postLikeMapper;
        this.commentMapper = commentMapper;
        this.commentLikeMapper = commentLikeMapper;
    }

    @Override
    @Transactional
    /**
     * 创建帖子
     */
    public Long createPost(Post post) {
        try {
            postMapper.insert(post);
            return post.getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    /**
     * 获取最新帖子列表
     */
    public List<Post> listRecentPosts() {
        try {
            return postMapper.findRecent();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    /**
     * 获取指定用户的帖子列表
     */
    public List<Post> listUserPosts(Long userId) {
        try {
            return postMapper.findByUserId(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    /**
     * 获取指定用户的媒体列表
     */
    public List<Media> listUserMedia(Long userId) {
        try {
            return mediaMapper.findByUserId(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    /**
     * 保存媒体信息
     */
    public Long saveMedia(Media media) {
        try {
            mediaMapper.insert(media);
            return media.getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    /**
     * 获取指定帖子的媒体列表
     */
    public List<Media> listMediaByPost(Long postId) {
        try {
            return mediaMapper.findByPostId(postId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    /**
     * 获取帖子详情
     */
    public Post getPost(Long id) {
        try {
            return postMapper.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    /**
     * 统计帖子点赞数
     */
    public int countPostLikes(Long postId) {
        return postLikeMapper.countByPostId(postId);
    }

    @Override
    @Transactional
    /**
     * 切换帖子点赞状态
     * 如果已点赞则取消，否则添加点赞。
     */
    public boolean togglePostLike(Long postId, Long userId) {
        Integer exists = postLikeMapper.exists(postId, userId);
        if (exists != null && exists > 0) {
            postLikeMapper.delete(postId, userId);
            return false;
        } else {
            postLikeMapper.insert(postId, userId);
            return true;
        }
    }

    @Override
    @Transactional
    /**
     * 添加评论
     */
    public Long addComment(Comment comment) {
        try {
            commentMapper.insert(comment);
            return comment.getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    /**
     * 获取帖子的评论列表
     */
    public List<Comment> listCommentsByPost(Long postId) {
        try {
            return commentMapper.findByPostId(postId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    /**
     * 统计帖子评论数
     */
    public int countComments(Long postId) {
        return commentMapper.countByPostId(postId);
    }

    @Override
    /**
     * 统计评论点赞数
     */
    public int countCommentLikes(Long commentId) {
        return commentLikeMapper.countByCommentId(commentId);
    }

    @Override
    @Transactional
    /**
     * 切换评论点赞状态
     */
    public boolean toggleCommentLike(Long commentId, Long userId) {
        Integer exists = commentLikeMapper.exists(commentId, userId);
        if (exists != null && exists > 0) {
            commentLikeMapper.delete(commentId, userId);
            return false;
        } else {
            commentLikeMapper.insert(commentId, userId);
            return true;
        }
    }

    @Override
    @Transactional
    /**
     * 删除帖子
     * 同时删除帖子相关的点赞、评论、媒体等数据。
     */
    public boolean deletePost(Long postId, Long userId) {
        Post p = postMapper.findById(postId);
        if (p == null || p.getUserId() == null || !p.getUserId().equals(userId)) {
            return false;
        }
        commentLikeMapper.deleteByPostId(postId);
        commentMapper.deleteByPostId(postId);
        postLikeMapper.deleteByPostId(postId);
        mediaMapper.deleteByPostId(postId);
        postMapper.deleteById(postId);
        return true;
    }
}
