package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Route;
import com.outdoor.demo.mapper.RouteMapper;
import com.outdoor.demo.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteMapper routeMapper;

    public RouteServiceImpl(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
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
}

