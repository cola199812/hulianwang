package com.outdoor.demo.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用户注册请求对象
 * 接收用户注册时的参数。
 */
public class UserRegisterRequest {
    /** 用户名 */
    @NotBlank
    private String username;
    /** 密码 */
    @NotBlank
    private String password;
    /** 邮箱地址 */
    @NotBlank
    @Email
    private String email;
    /** 验证码 */
    @NotBlank
    private String verificationCode;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
