package com.outdoor.demo.entity;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录请求对象
 * 接收用户登录时的参数。
 */
public class UserLoginRequest {
    /** 用户名或邮箱 */
    @NotBlank
    private String username;
    /** 密码 */
    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

