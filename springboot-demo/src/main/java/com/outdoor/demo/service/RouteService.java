package com.outdoor.demo.service;

import com.outdoor.demo.entity.Route;

import java.util.List;

public interface RouteService {
    Long create(Route route);
    Route getById(Long id);
    List<Route> listAll();
}

