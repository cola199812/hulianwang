package com.outdoor.demo.service;

import com.outdoor.demo.entity.Route;

import java.util.List;

/**
 * 路线服务接口
 * 定义路线相关的业务逻辑方法。
 */
public interface RouteService {
    /**
     * 创建路线
     * @param route 路线对象
     * @return 路线ID
     */
    Long create(Route route);

    /**
     * 根据ID获取路线
     * @param id 路线ID
     * @return 路线对象
     */
    Route getById(Long id);

    /**
     * 获取所有路线
     * @return 路线列表
     */
    List<Route> listAll();
}

