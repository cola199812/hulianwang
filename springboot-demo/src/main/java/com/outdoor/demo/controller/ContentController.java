package com.outdoor.demo.controller;

import com.outdoor.demo.entity.Media;
import com.outdoor.demo.entity.Post;
import com.outdoor.demo.entity.PostImage;
import com.outdoor.demo.entity.PostVideo;
import com.outdoor.demo.entity.Comment;
import com.outdoor.demo.service.ContentService;
import com.outdoor.demo.service.MinioService;
import com.outdoor.demo.utils.VideoUtils;
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
    private final MinioService minioService;

    public ContentController(ContentService contentService, MinioService minioService) {
        this.contentService = contentService;
        this.minioService = minioService;
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
    public ResponseEntity<?> listPosts(HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        List<Post> list = contentService.listRecentPosts(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/post/my")
    public ResponseEntity<?> listMyPosts(HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        List<Post> list = contentService.listUserPosts(userId, userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/post/nearby")
    public ResponseEntity<?> listNearbyPosts(@RequestParam("lat") Double lat,
                                             @RequestParam("lng") Double lng,
                                             @RequestParam(value = "radius", defaultValue = "5000") Double radius,
                                             HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        List<Post> list = contentService.listNearbyPosts(lat, lng, radius, userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/post/popular")
    public ResponseEntity<?> listPopularPosts(HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        return ResponseEntity.ok(contentService.listPopularPosts(userId));
    }

    @GetMapping("/topic/list")
    public ResponseEntity<?> listTopics() {
        return ResponseEntity.ok(contentService.listTopics());
    }

    @PostMapping("/post/{id}/view")
    public ResponseEntity<?> viewPost(@PathVariable("id") Long id) {
        contentService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id, HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        Post p = contentService.getPost(id, userId);
        if (p == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未找到");
            return ResponseEntity.status(404).body(body);
        }
        contentService.incrementViewCount(id); // Increment view count on fetch
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
    public ResponseEntity<?> listComments(@PathVariable("postId") Long postId, HttpSession session) {
        Object uid = session.getAttribute("userId");
        Long userId = uid instanceof Long ? (Long) uid : 0L;
        List<Comment> list = contentService.listCommentsByPost(postId, userId);
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

    @GetMapping("/upload/presigned-url")
    public ResponseEntity<?> getPresignedUrl(@RequestParam("objectName") String objectName) {
        try {
            String presignedUrl = minioService.getPresignedUrl(objectName);
            return ResponseEntity.ok(Map.of("presignedUrl", presignedUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "获取上传凭证失败: " + e.getMessage()));
        }
    }

    @PostMapping("/post/{id}/images")
    public ResponseEntity<?> saveImages(@PathVariable("id") Long postId, @RequestBody List<PostImage> images) {
        if (images == null || images.isEmpty()) {
             return ResponseEntity.badRequest().body(Map.of("message", "图片列表为空"));
        }
        for (PostImage img : images) {
            img.setPostId(postId);
        }
        contentService.savePostImages(images);
        return ResponseEntity.ok(Map.of("message", "图片保存成功"));
    }

    @PostMapping("/post/{id}/video")
    public ResponseEntity<?> saveVideo(@PathVariable("id") Long postId, @RequestBody PostVideo video) {
        video.setPostId(postId);
        
        // Auto generate cover if missing
        if (video.getCoverUrl() == null || video.getCoverUrl().isEmpty()) {
             try {
                 String tempCoverName = UUID.randomUUID().toString() + ".jpg";
                 File tempFile = File.createTempFile("cover", ".jpg");
                 String tempPath = tempFile.getAbsolutePath();
                 
                 String videoUrl = video.getVideoUrl();
                 // Convert to internal URL for ffmpeg
                 if (videoUrl.startsWith("http")) {
                     videoUrl = minioService.getInternalUrl(videoUrl);
                 } else {
                     // If not http, assume object name and construct url (or get presigned GET if needed)
                     // For now assume public or handled by MinioService
                     videoUrl = minioService.getObjectUrl(videoUrl); 
                     // Ensure we use internal url
                     videoUrl = minioService.getInternalUrl(videoUrl);
                 }
                 
                 boolean success = VideoUtils.generateCover(videoUrl, tempPath);
                 if (success) {
                     String objectName = "covers/" + tempCoverName;
                     try (java.io.FileInputStream fis = new java.io.FileInputStream(tempFile)) {
                         String coverUrl = minioService.uploadFile(fis, objectName, "image/jpeg");
                         video.setCoverUrl(coverUrl);
                     }
                 }
                 tempFile.delete();
             } catch (Exception e) {
                 e.printStackTrace();
             }
        }
        
        contentService.savePostVideo(video);
        return ResponseEntity.ok(Map.of("message", "视频保存成功"));
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
