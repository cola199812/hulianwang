package com.outdoor.demo.controller;

import com.outdoor.demo.entity.Route;
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
}

