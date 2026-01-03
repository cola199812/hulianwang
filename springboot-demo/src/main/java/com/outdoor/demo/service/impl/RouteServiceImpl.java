package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Route;
import com.outdoor.demo.entity.RouteComment;
import com.outdoor.demo.mapper.RouteCommentMapper;
import com.outdoor.demo.mapper.RouteFavoriteMapper;
import com.outdoor.demo.mapper.RouteMapper;
import com.outdoor.demo.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteMapper routeMapper;
    private final RouteCommentMapper routeCommentMapper;
    private final RouteFavoriteMapper routeFavoriteMapper;

    public RouteServiceImpl(RouteMapper routeMapper, RouteCommentMapper routeCommentMapper,
            RouteFavoriteMapper routeFavoriteMapper) {
        this.routeMapper = routeMapper;
        this.routeCommentMapper = routeCommentMapper;
        this.routeFavoriteMapper = routeFavoriteMapper;
    }

    @Override
    @Transactional
    public Long create(Route route) {
        routeMapper.insert(route);
        return route.getId();
    }

    @Override
    public Route getById(Long id) {
        return routeMapper.findById(id);
    }

    @Override
    public List<Route> listAll() {
        return routeMapper.findAll();
    }

    @Override
    public List<Route> listMyRoutes(Long creatorId) {
        return routeMapper.findMyRoutes(creatorId);
    }

    @Override
    @Transactional
    public void updateRoute(Long id, Route route) {
        route.setId(id);
        routeMapper.update(route);
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        routeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Long addComment(RouteComment comment) {
        routeCommentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    public List<RouteComment> getComments(Long routeId) {
        return routeCommentMapper.findByRouteId(routeId);
    }

    @Override
    @Transactional
    public void likeComment(Long commentId) {
        routeCommentMapper.incrementLikeCount(commentId);
    }

    @Override
    public Map<String, Object> getRatingStats(Long routeId) {
        List<Map<String, Object>> stats = routeCommentMapper.getRatingStats(routeId);
        Map<String, Object> result = new HashMap<>();
        result.put("1", 0);
        result.put("2", 0);
        result.put("3", 0);
        result.put("4", 0);
        result.put("5", 0);

        for (Map<String, Object> stat : stats) {
            String rating = String.valueOf(stat.get("rating"));
            Long count = (Long) stat.get("count");
            result.put(rating, count.intValue());
        }

        return result;
    }

    @Override
    @Transactional
    public void toggleFavorite(Long routeId, Long userId) {
        if (routeFavoriteMapper.isFavorited(routeId, userId)) {
            routeFavoriteMapper.removeFavorite(routeId, userId);
        } else {
            routeFavoriteMapper.addFavorite(routeId, userId);
        }
    }

    @Override
    public boolean checkFavorite(Long routeId, Long userId) {
        return routeFavoriteMapper.isFavorited(routeId, userId);
    }
}
