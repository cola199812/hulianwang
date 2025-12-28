package com.outdoor.demo.service;

import com.outdoor.demo.entity.User;
import com.outdoor.demo.entity.UserRegisterRequest;

/**
 * 用户服务接口
 * 定义用户注册、登录、信息管理等业务逻辑方法。
 */
public interface UserService {
    /**
     * 用户注册
     * @param req 注册请求信息
     * @return 注册成功的用户对象
     */
    User register(UserRegisterRequest req);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象，失败返回null
     */
    User login(String username, String password);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象
     */
    User getById(Long id);

    /**
     * 重置密码
     * @param email 邮箱
     * @param code 验证码
     * @param newPassword 新密码
     */
    void resetPassword(String email, String code, String newPassword);
}

