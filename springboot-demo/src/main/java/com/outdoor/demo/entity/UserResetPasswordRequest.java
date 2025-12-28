package com.outdoor.demo.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 重置密码请求对象
 * 接收用户重置密码时的参数。
 */
public class UserResetPasswordRequest {
    /** 邮箱地址 */
    @NotBlank
    @Email
    private String email;
    /** 验证码 */
    @NotBlank
    private String verificationCode;
    /** 新密码 */
    @NotBlank
    private String newPassword;

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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
