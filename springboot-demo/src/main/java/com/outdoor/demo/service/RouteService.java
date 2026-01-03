package com.outdoor.demo.service;

import com.outdoor.demo.entity.Route;
import com.outdoor.demo.entity.RouteComment;

import java.util.List;
import java.util.Map;

public interface RouteService {
    Long create(Route route);

    Route getById(Long id);

    List<Route> listAll();

    List<Route> listMyRoutes(Long creatorId);

    void updateRoute(Long id, Route route);

    void deleteRoute(Long id);

    // 评论相关方法
    Long addComment(RouteComment comment);

    List<RouteComment> getComments(Long routeId);

    void likeComment(Long commentId);

    Map<String, Object> getRatingStats(Long routeId);

    // 收藏相关方法
    void toggleFavorite(Long routeId, Long userId);

    boolean checkFavorite(Long routeId, Long userId);
}
