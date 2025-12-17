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
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

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

    @GetMapping("/post/list")
    public ResponseEntity<?> listPosts() {
        List<Post> list = contentService.listRecentPosts();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/post/my")
    public ResponseEntity<?> listMyPosts(HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        List<Post> list = contentService.listUserPosts(userId);
        return ResponseEntity.ok(list);
    }

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

    @GetMapping("/post/{id}/stats")
    public ResponseEntity<?> postStats(@PathVariable("id") Long id) {
        Map<String, Object> body = new HashMap<>();
        body.put("likeCount", contentService.countPostLikes(id));
        body.put("commentCount", contentService.countComments(id));
        return ResponseEntity.ok(body);
    }

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

    @GetMapping("/comment/list/{postId}")
    public ResponseEntity<?> listComments(@PathVariable("postId") Long postId) {
        List<Comment> list = contentService.listCommentsByPost(postId);
        return ResponseEntity.ok(list);
    }

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

    @GetMapping("/comment/{id}/stats")
    public ResponseEntity<?> commentStats(@PathVariable("id") Long commentId) {
        Map<String, Object> body = new HashMap<>();
        body.put("likeCount", contentService.countCommentLikes(commentId));
        return ResponseEntity.ok(body);
    }
    
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

    @GetMapping("/media/by-post/{postId}")
    public ResponseEntity<?> listMediaByPost(@PathVariable("postId") Long postId) {
        List<com.outdoor.demo.entity.Media> list = contentService.listMediaByPost(postId);
        return ResponseEntity.ok(list);
    }

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
