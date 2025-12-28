package com.outdoor.demo.service.impl;

import com.outdoor.demo.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
/**
 * 验证码服务实现类
 * 实现验证码的发送和验证逻辑。
 */
public class VerificationCodeServiceImpl implements VerificationCodeService {

    // Store codes: Email -> CodeInfo
    // For simplicity in this demo, we use a ConcurrentHashMap.
    // In production, use Redis.
    private final Map<String, CodeInfo> codeStorage = new ConcurrentHashMap<>();

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static class CodeInfo {
        String code;
        long expiryTime;
        long lastSentTime;

        CodeInfo(String code, long expiryTime, long lastSentTime) {
            this.code = code;
            this.expiryTime = expiryTime;
            this.lastSentTime = lastSentTime;
        }
    }

    @Override
    /**
     * 发送验证码
     * 生成随机验证码并发送到指定邮箱，包含发送频率限制（1分钟）和有效期（5分钟）。
     */
    public void sendCode(String email) {
        // 检查发送间隔（1分钟）
        CodeInfo existing = codeStorage.get(email);
        long now = System.currentTimeMillis();
        if (existing != null) {
            long elapsed = now - existing.lastSentTime;
            if (elapsed < 60 * 1000) {
                throw new RuntimeException("操作过于频繁，请" + (60 - elapsed / 1000) + "秒后再试");
            }
        }

        // Generate 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));

        // Save to storage (valid for 5 minutes)
        codeStorage.put(email, new CodeInfo(code, now + 5 * 60 * 1000, now));

        // Send email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("【青年户外社交平台】注册验证码");
            message.setText("您的验证码是：" + code + "，有效期5分钟。请勿泄露给他人。");
            mailSender.send(message);
            System.out.println("邮件已发送至: " + email + "，请前往邮箱查看");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("邮件发送失败: " + e.getMessage());
            throw new RuntimeException("邮件发送失败，请检查后端配置");
        }
    }

    @Override
    /**
     * 验证验证码
     * 校验验证码是否正确且在有效期内。
     */
    public boolean verifyCode(String email, String code) {
        CodeInfo info = codeStorage.get(email);
        if (info == null) {
            return false;
        }
        if (System.currentTimeMillis() > info.expiryTime) {
            codeStorage.remove(email);
            return false;
        }
        if (info.code.equals(code)) {
            codeStorage.remove(email); // Invalidate after use
            return true;
        }
        return false;
    }
}
