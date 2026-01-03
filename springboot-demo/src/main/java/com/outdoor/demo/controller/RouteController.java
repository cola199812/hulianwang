package com.outdoor.demo.controller;

import com.outdoor.demo.entity.Route;
import com.outdoor.demo.entity.RouteComment;
import com.outdoor.demo.entity.RouteCreateRequest;
import com.outdoor.demo.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/route")
@Validated
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<Route> routes = routeService.listAll();
        return ResponseEntity.ok(routes);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody RouteCreateRequest req, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }
        Route r = new Route();
        r.setName(req.getName());
        r.setDistance(req.getDistance());
        r.setLevel(req.getLevel());
        r.setDescription(req.getDescription());
        r.setCreatorId((Long) uid);
        Long id = routeService.create(r);
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("message", "创建成功");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Long id) {
        Route r = routeService.getById(id);
        if (r == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未找到该路线");
            return ResponseEntity.status(404).body(body);
        }
        return ResponseEntity.ok(r);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> addComment(@PathVariable("id") Long routeId,
            @RequestBody Map<String, Object> req,
            HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        RouteComment comment = new RouteComment();
        comment.setRouteId(routeId);
        comment.setUserId((Long) uid);
        comment.setUserName((String) req.get("userName"));
        comment.setUserAvatar((String) req.get("userAvatar"));
        comment.setContent((String) req.get("content"));
        comment.setRating((Integer) req.get("rating"));

        Long id = routeService.addComment(comment);
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("message", "评论添加成功");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<?> getComments(@PathVariable("id") Long routeId) {
        List<RouteComment> comments = routeService.getComments(routeId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable("commentId") Long commentId) {
        routeService.likeComment(commentId);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "点赞成功");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}/rating-stats")
    public ResponseEntity<?> getRatingStats(@PathVariable("id") Long routeId) {
        Map<String, Object> stats = routeService.getRatingStats(routeId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable("id") Long routeId, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        routeService.toggleFavorite(routeId, (Long) uid);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "操作成功");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}/favorite")
    public ResponseEntity<?> checkFavorite(@PathVariable("id") Long routeId, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("favorited", false);
            return ResponseEntity.ok(body);
        }

        boolean favorited = routeService.checkFavorite(routeId, (Long) uid);
        Map<String, Object> body = new HashMap<>();
        body.put("favorited", favorited);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/my-routes")
    public ResponseEntity<?> listMyRoutes(HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        List<Route> routes = routeService.listMyRoutes((Long) uid);
        return ResponseEntity.ok(routes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable("id") Long id,
            @Valid @RequestBody RouteCreateRequest req,
            HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        Route route = new Route();
        route.setName(req.getName());
        route.setDistance(req.getDistance());
        route.setLevel(req.getLevel());
        route.setDescription(req.getDescription());

        routeService.updateRoute(id, route);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "更新成功");
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable("id") Long id, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }

        routeService.deleteRoute(id);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "删除成功");
        return ResponseEntity.ok(body);
    }
}
