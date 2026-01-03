package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Post;
import com.outdoor.demo.entity.Media;
import com.outdoor.demo.entity.PostImage;
import com.outdoor.demo.entity.PostVideo;
import com.outdoor.demo.entity.Comment;
import com.outdoor.demo.mapper.PostMapper;
import com.outdoor.demo.mapper.MediaMapper;
import com.outdoor.demo.mapper.PostImageMapper;
import com.outdoor.demo.mapper.PostVideoMapper;
import com.outdoor.demo.mapper.PostLikeMapper;
import com.outdoor.demo.mapper.CommentMapper;
import com.outdoor.demo.mapper.CommentLikeMapper;
import com.outdoor.demo.service.ContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outdoor.demo.mapper.TopicMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ContentServiceImpl implements ContentService {
    private final PostMapper postMapper;
    private final MediaMapper mediaMapper;
    private final PostLikeMapper postLikeMapper;
    private final CommentMapper commentMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final PostImageMapper postImageMapper;
    private final PostVideoMapper postVideoMapper;
    private final TopicMapper topicMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_POPULAR = "post:popularity";

    public ContentServiceImpl(PostMapper postMapper, MediaMapper mediaMapper,
                              PostLikeMapper postLikeMapper, CommentMapper commentMapper, CommentLikeMapper commentLikeMapper,
                              PostImageMapper postImageMapper, PostVideoMapper postVideoMapper,
                              TopicMapper topicMapper, RedisTemplate<String, Object> redisTemplate) {
        this.postMapper = postMapper;
        this.mediaMapper = mediaMapper;
        this.postLikeMapper = postLikeMapper;
        this.commentMapper = commentMapper;
        this.commentLikeMapper = commentLikeMapper;
        this.postImageMapper = postImageMapper;
        this.postVideoMapper = postVideoMapper;
        this.topicMapper = topicMapper;
        this.redisTemplate = redisTemplate;
    }

    private void processTopics(Post post) {
        java.util.Set<String> uniqueTopics = new java.util.HashSet<>();

        // 1. Extract from Markdown
        if (post.getMarkdown() != null) {
            Pattern pattern = Pattern.compile("#(\\S+)");
            Matcher matcher = pattern.matcher(post.getMarkdown());
            while (matcher.find()) {
                String name = matcher.group(1);
                if (name.length() > 50) name = name.substring(0, 50);
                uniqueTopics.add(name);
            }
        }

        // 2. Extract from explicit topics list (from UI selection)
        if (post.getTopics() != null) {
            for (com.outdoor.demo.entity.Topic t : post.getTopics()) {
                if (t.getName() != null && !t.getName().isEmpty()) {
                    String name = t.getName();
                    if (name.length() > 50) name = name.substring(0, 50);
                    uniqueTopics.add(name);
                }
            }
        }

        // 3. Save to DB
        for (String topicName : uniqueTopics) {
            try {
                // Find or create topic
                com.outdoor.demo.entity.Topic topic = topicMapper.findByName(topicName);
                if (topic == null) {
                    topic = new com.outdoor.demo.entity.Topic();
                    topic.setName(topicName);
                    topicMapper.insert(topic);
                }
                // Link post to topic
                topicMapper.insertPostTopic(post.getId(), topic.getId());
            } catch (Exception e) {
                // Ignore duplicates
            }
        }
    }

    private void updatePopularity(Long postId, double scoreDelta) {
        try {
            redisTemplate.opsForZSet().incrementScore(REDIS_KEY_POPULAR, postId, scoreDelta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public Long createPost(Post post) {
        try {
            postMapper.insert(post);
            processTopics(post);
            return post.getId();
        } catch (Exception e) {
            return null;
        }
    }

    private void enrichPostWithMedia(Post post, Long currentUserId) {
        if (post == null) return;
        try {
            post.setImages(postImageMapper.findByPostId(post.getId()));
            List<PostVideo> videos = postVideoMapper.findByPostId(post.getId());
            if (videos != null && !videos.isEmpty()) {
                post.setVideo(videos.get(0));
            }
            // Populate topics
            post.setTopics(topicMapper.findByPostId(post.getId()));
            
            // Check if liked by current user
            if (currentUserId != null) {
                Integer count = postLikeMapper.exists(post.getId(), currentUserId);
                post.setIsLiked(count != null && count > 0);
            } else {
                post.setIsLiked(false);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void enrichPosts(List<Post> posts, Long currentUserId) {
        if (posts == null) return;
        for (Post post : posts) {
            enrichPostWithMedia(post, currentUserId);
        }
    }

    @Override
    public List<Post> listRecentPosts(Long currentUserId) {
        try {
            List<Post> posts = postMapper.findRecent();
            enrichPosts(posts, currentUserId);
            return posts;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Post> listUserPosts(Long userId, Long currentUserId) {
        try {
            List<Post> posts = postMapper.findByUserId(userId);
            enrichPosts(posts, currentUserId);
            return posts;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Post> listNearbyPosts(Double lat, Double lng, Double radius, Long currentUserId) {
        try {
            List<Post> posts = postMapper.findNearby(lat, lng, radius);
            enrichPosts(posts, currentUserId);
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
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
    @Transactional
    public void savePostImages(List<PostImage> images) {
        if (images == null || images.isEmpty()) return;
        for (PostImage img : images) {
            postImageMapper.insert(img);
        }
    }

    @Override
    @Transactional
    public void savePostVideo(PostVideo video) {
        if (video == null) return;
        postVideoMapper.insert(video);
    }

    @Override
    public List<PostImage> listPostImages(Long postId) {
        try {
            return postImageMapper.findByPostId(postId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public PostVideo getPostVideo(Long postId) {
        try {
            List<PostVideo> list = postVideoMapper.findByPostId(postId);
            return (list != null && !list.isEmpty()) ? list.get(0) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Post getPost(Long id, Long currentUserId) {
        try {
            Post post = postMapper.findById(id);
            enrichPostWithMedia(post, currentUserId);
            return post;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int countPostLikes(Long postId) {
        return postLikeMapper.countByPostId(postId);
    }

    @Override
    @Transactional
    public boolean togglePostLike(Long postId, Long userId) {
        Integer exists = postLikeMapper.exists(postId, userId);
        if (exists != null && exists > 0) {
            postLikeMapper.delete(postId, userId);
            updatePopularity(postId, -2.0); // Decrease score
            return false;
        } else {
            postLikeMapper.insert(postId, userId);
            updatePopularity(postId, 2.0); // Increase score
            return true;
        }
    }

    @Override
    @Transactional
    public Long addComment(Comment comment) {
        try {
            commentMapper.insert(comment);
            updatePopularity(comment.getPostId(), 3.0); // Comment adds 3 points
            return comment.getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<com.outdoor.demo.entity.Topic> listTopics() {
        return topicMapper.findHotTopics();
    }

    @Override
    public List<Post> listPopularPosts(Long currentUserId) {
        try {
            // Get top 20 IDs from Redis
            Set<Object> ids = redisTemplate.opsForZSet().reverseRange(REDIS_KEY_POPULAR, 0, 19);
            if (ids == null || ids.isEmpty()) {
                // Fallback to DB if Redis is empty (or initial state)
                return listRecentPosts(currentUserId); 
            }
            List<Long> postIds = ids.stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .collect(Collectors.toList());
            
            List<Post> posts = postMapper.findByIds(postIds);
            enrichPosts(posts, currentUserId);
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void incrementViewCount(Long postId) {
        postMapper.incrementViewCount(postId);
        updatePopularity(postId, 1.0); // View adds 1 point
    }

    @Override
    public List<Comment> listCommentsByPost(Long postId, Long currentUserId) {
        try {
            List<Comment> comments = commentMapper.findByPostId(postId);
            if (comments != null && !comments.isEmpty()) {
                for (Comment c : comments) {
                    c.setLikeCount(commentLikeMapper.countByCommentId(c.getId()));
                    if (currentUserId != null) {
                        Integer exists = commentLikeMapper.exists(c.getId(), currentUserId);
                        c.setIsLiked(exists != null && exists > 0);
                    } else {
                        c.setIsLiked(false);
                    }
                }
            }
            return comments;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public int countComments(Long postId) {
        return commentMapper.countByPostId(postId);
    }

    @Override
    public int countCommentLikes(Long commentId) {
        return commentLikeMapper.countByCommentId(commentId);
    }

    @Override
    @Transactional
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
