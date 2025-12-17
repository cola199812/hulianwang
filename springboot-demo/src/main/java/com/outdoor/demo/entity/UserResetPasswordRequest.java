package com.outdoor.demo.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserResetPasswordRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String verificationCode;
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
