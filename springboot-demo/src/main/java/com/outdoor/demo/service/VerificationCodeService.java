package com.outdoor.demo.service;

public interface VerificationCodeService {
    void sendCode(String email);
    boolean verifyCode(String email, String code);
}
