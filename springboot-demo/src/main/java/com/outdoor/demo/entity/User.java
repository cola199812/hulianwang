package com.outdoor.demo.entity;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库中的user表，存储用户的基本信息。
 */
public class User {
    /** 用户ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 加密后的密码 */
    private String password;
    /** 邮箱地址 */
    private String email;
    /** 注册时间 */
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}

