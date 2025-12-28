package com.outdoor.demo.service;

/**
 * 验证码服务接口
 * 定义验证码发送和验证的业务逻辑方法。
 */
public interface VerificationCodeService {
    /**
     * 发送验证码
     * @param email 接收验证码的邮箱
     */
    void sendCode(String email);

    /**
     * 验证验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 验证是否通过
     */
    boolean verifyCode(String email, String code);
}
