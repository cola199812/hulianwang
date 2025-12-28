package com.outdoor.demo.controller;

import com.outdoor.demo.entity.Media;
import com.outdoor.demo.entity.Post;
import com.outdoor.demo.entity.Comment;
import com.outdoor.demo.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/content")
@Validated
/**
 * 内容控制器
 * 处理帖子、评论、点赞和媒体上传等相关功能的HTTP请求。
 */
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * 发布帖子
     * 用户发布新的帖子，支持设置标题、内容、位置等信息。
     *
     * @param post 帖子对象
     * @param session HTTP会话
     * @return 发布结果
     */
    @PostMapping("/post/create")
    public ResponseEntity<?> createPost(@RequestBody Post post, HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        if (post.getTitle() == null) post.setTitle("");
        if (post.getMarkdown() == null) post.setMarkdown("");
        if (post.getLocationName() == null) post.setLocationName("");
        if (post.getCoverUrl() == null) post.setCoverUrl("");
        if (post.getLat() == null) post.setLat(0D);
        if (post.getLng() == null) post.setLng(0D);
        post.setUserId(userId);
        Long id = contentService.createPost(post);
        if (id == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "发布失败，请检查数据库表是否存在或字段允许空值");
            return ResponseEntity.status(500).body(body);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("message", "发布成功");
        return ResponseEntity.ok(body);
    }

    /**
     * 获取最新帖子列表
     *
     * @return 帖子列表
     */
    @GetMapping("/post/list")
    public ResponseEntity<?> listPosts() {
        List<Post> list = contentService.listRecentPosts();
        return ResponseEntity.ok(list);
    }

    /**
     * 获取我的帖子列表
     * 获取当前登录用户发布的帖子。
     *
     * @param session HTTP会话
     * @return 用户帖子列表
     */
    @GetMapping("/post/my")
    public ResponseEntity<?> listMyPosts(HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        List<Post> list = contentService.listUserPosts(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * 获取帖子详情
     * 根据ID获取单个帖子的详细信息。
     *
     * @param id 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id) {
        Post p = contentService.getPost(id);
        if (p == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未找到");
            return ResponseEntity.status(404).body(body);
        }
        return ResponseEntity.ok(p);
    }

    /**
     * 获取帖子统计信息
     * 获取帖子的点赞数和评论数。
     *
     * @param id 帖子ID
     * @return 统计信息
     */
    @GetMapping("/post/{id}/stats")
    public ResponseEntity<?> postStats(@PathVariable("id") Long id) {
        Map<String, Object> body = new HashMap<>();
        body.put("likeCount", contentService.countPostLikes(id));
        body.put("commentCount", contentService.countComments(id));
        return ResponseEntity.ok(body);
    }

    /**
     * 点赞/取消点赞帖子
     *
     * @param postId 帖子ID
     * @param session HTTP会话
     * @return 点赞状态和最新点赞数
     */
    @PostMapping("/post/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable("id") Long postId, HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        boolean liked = contentService.togglePostLike(postId, userId);
        Map<String, Object> body = new HashMap<>();
        body.put("liked", liked);
        body.put("likeCount", contentService.countPostLikes(postId));
        return ResponseEntity.ok(body);
    }

    /**
     * 获取评论列表
     * 获取指定帖子的所有评论。
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    @GetMapping("/comment/list/{postId}")
    public ResponseEntity<?> listComments(@PathVariable("postId") Long postId) {
        List<Comment> list = contentService.listCommentsByPost(postId);
        return ResponseEntity.ok(list);
    }

    /**
     * 添加评论
     *
     * @param comment 评论对象
     * @param session HTTP会话
     * @return 新评论ID
     */
    @PostMapping("/comment/add")
    public ResponseEntity<?> addComment(@RequestBody Comment comment, HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        comment.setUserId(userId);
        if (comment.getContent() == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "内容不能为空");
            return ResponseEntity.badRequest().body(body);
        }
        Long id = contentService.addComment(comment);
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return ResponseEntity.ok(body);
    }

    /**
     * 点赞/取消点赞评论
     *
     * @param commentId 评论ID
     * @param session HTTP会话
     * @return 点赞状态和最新点赞数
     */
    @PostMapping("/comment/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable("id") Long commentId, HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        boolean liked = contentService.toggleCommentLike(commentId, userId);
        Map<String, Object> body = new HashMap<>();
        body.put("liked", liked);
        body.put("likeCount", contentService.countCommentLikes(commentId));
        return ResponseEntity.ok(body);
    }

    /**
     * 获取评论统计信息
     *
     * @param commentId 评论ID
     * @return 点赞数
     */
    @GetMapping("/comment/{id}/stats")
    public ResponseEntity<?> commentStats(@PathVariable("id") Long commentId) {
        Map<String, Object> body = new HashMap<>();
        body.put("likeCount", contentService.countCommentLikes(commentId));
        return ResponseEntity.ok(body);
    }
    
    /**
     * 删除帖子
     *
     * @param postId 帖子ID
     * @param session HTTP会话
     * @return 删除结果
     */
    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long postId, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }
        boolean ok = contentService.deletePost(postId, (Long) uid);
        Map<String, Object> body = new HashMap<>();
        if (ok) {
            body.put("message", "删除成功");
            return ResponseEntity.ok(body);
        } else {
            body.put("message", "无权删除或帖子不存在");
            return ResponseEntity.status(403).body(body);
        }
    }

    /**
     * 获取用户媒体列表
     *
     * @param session HTTP会话
     * @return 媒体文件列表
     */
    @GetMapping("/media/list")
    public ResponseEntity<?> listMedia(HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }
        List<com.outdoor.demo.entity.Media> list = contentService.listUserMedia((Long) uid);
        return ResponseEntity.ok(list);
    }

    /**
     * 获取帖子媒体列表
     *
     * @param postId 帖子ID
     * @return 媒体文件列表
     */
    @GetMapping("/media/by-post/{postId}")
    public ResponseEntity<?> listMediaByPost(@PathVariable("postId") Long postId) {
        List<com.outdoor.demo.entity.Media> list = contentService.listMediaByPost(postId);
        return ResponseEntity.ok(list);
    }

    /**
     * 上传文件
     * 上传图片或视频文件，并保存到服务器。
     *
     * @param file 上传的文件
     * @param type 文件类型
     * @param postId 关联帖子ID
     * @param album 相册名称
     * @param tags 标签
     * @param session HTTP会话
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "type", required = false) String type,
                                    @RequestParam(value = "postId", required = false) Long postId,
                                    @RequestParam(value = "album", required = false) String album,
                                    @RequestParam(value = "tags", required = false) String tags,
                                    HttpSession session) throws IOException {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        String root = System.getProperty("user.dir") + "/springboot-demo/uploads/";
        File dir = new File(root);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File(dir, name);
        file.transferTo(dest);
        String url = "/uploads/" + name;
        Media m = new Media();
        m.setUserId(userId);
        m.setPostId(postId);
        m.setType(type == null ? "image" : type);
        m.setUrl(url);
        m.setAlbum(album);
        m.setTags(tags);
        contentService.saveMedia(m);
        Map<String, Object> body = new HashMap<>();
        body.put("url", url);
        return ResponseEntity.ok(body);
    }
}
