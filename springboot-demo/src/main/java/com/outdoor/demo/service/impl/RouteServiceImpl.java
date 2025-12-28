package com.outdoor.demo.service.impl;

import com.outdoor.demo.entity.Route;
import com.outdoor.demo.mapper.RouteMapper;
import com.outdoor.demo.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/**
 * 路线服务实现类
 * 实现路线相关的业务逻辑。
 */
public class RouteServiceImpl implements RouteService {
    private final RouteMapper routeMapper;

    public RouteServiceImpl(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    @Override
    @Transactional
    /**
     * 创建路线
     */
    public Long create(Route route) {
        routeMapper.insert(route);
        return route.getId();
    }

    @Override
    /**
     * 根据ID获取路线
     */
    public Route getById(Long id) {
        return routeMapper.findById(id);
    }

    @Override
    /**
     * 获取所有路线
     */
    public List<Route> listAll() {
        return routeMapper.findAll();
    }
}

