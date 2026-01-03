package com.outdoor.demo.controller;

import com.outdoor.demo.entity.User;
import com.outdoor.demo.entity.UserLoginRequest;
import com.outdoor.demo.entity.UserRegisterRequest;
import com.outdoor.demo.entity.UserResetPasswordRequest;
import com.outdoor.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Validated
/**
 * 用户控制器
 * 处理用户注册、登录、信息查询、密码重置等相关功能的HTTP请求。
 */
public class UserController {
    private final UserService userService;
    private final com.outdoor.demo.service.VerificationCodeService verificationCodeService;

    public UserController(UserService userService, com.outdoor.demo.service.VerificationCodeService verificationCodeService) {
        this.userService = userService;
        this.verificationCodeService = verificationCodeService;
    }

    /**
     * 发送验证码
     * 向指定邮箱发送验证码，用于注册或重置密码。
     *
     * @param email 邮箱地址
     * @return 发送结果
     */
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        verificationCodeService.sendCode(email);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "验证码已发送");
        return ResponseEntity.ok(body);
    }

    /**
     * 用户注册
     *
     * @param req 注册请求对象
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest req) {
        try {
            userService.register(req);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "注册成功");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 重置密码
     *
     * @param req 重置密码请求对象
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody UserResetPasswordRequest req) {
        try {
            userService.resetPassword(req.getEmail(), req.getVerificationCode(), req.getNewPassword());
            Map<String, Object> body = new HashMap<>();
            body.put("message", "密码重置成功");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 处理参数校验异常
     * 当请求参数不符合验证规则时，捕获异常并返回友好的错误信息。
     *
     * @param ex 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        // 获取第一个错误信息
        String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        body.put("message", errorMsg);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 用户登录
     *
     * @param req 登录请求对象
     * @param session HTTP会话
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest req, HttpSession session) {
        User user = userService.login(req.getUsername(), req.getPassword());
        if (user != null) {
            session.setAttribute("userId", user.getId());
            Map<String, Object> body = new HashMap<>();
            body.put("message", "登录成功");
            return ResponseEntity.ok(body);
        } else {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "登录失败，用户名或密码错误");
            return ResponseEntity.status(401).body(body);
        }
    }

    /**
     * 获取当前用户信息
     *
     * @param session HTTP会话
     * @return 用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<?> info(HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }
        User u = userService.getById((Long) uid);
        Map<String, Object> body = new HashMap<>();
        body.put("id", u.getId());
        body.put("username", u.getUsername());
        body.put("email", u.getEmail());
        body.put("nickname", u.getNickname());
        body.put("avatarUrl", u.getAvatarUrl());
        body.put("gender", u.getGender());
        body.put("birthday", u.getBirthday());
        body.put("bio", u.getBio());
        body.put("phone", u.getPhone());
        body.put("createTime", u.getCreateTime());
        return ResponseEntity.ok(body);
    }

    /**
     * 更新用户个人信息
     *
     * @param user 用户信息
     * @param session HTTP会话
     * @return 更新结果
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User user, HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "未登录");
            return ResponseEntity.status(401).body(body);
        }
        
        // 确保只能更新自己的信息
        user.setId((Long) uid);
        
        try {
            User updatedUser = userService.updateUserProfile(user);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "个人信息更新成功");
            body.put("user", updatedUser);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 获取简略用户信息
     * 根据用户ID获取公开的简单信息。
     *
     * @param id 用户ID
     * @return 简略用户信息
     */
    @GetMapping("/simple/{id}")
    public ResponseEntity<?> simple(@PathVariable("id") Long id) {
        User u = userService.getById(id);
        if (u == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("id", 0L);
            body.put("username", "匿名用户");
            return ResponseEntity.ok(body);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("id", u.getId());
        body.put("username", u.getUsername());
        return ResponseEntity.ok(body);
    }
    
    /**
     * 退出登录
     *
     * @param session HTTP会话
     * @return 退出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.removeAttribute("userId");
        Map<String, Object> body = new HashMap<>();
        body.put("message", "已退出登录");
        return ResponseEntity.ok(body);
    }
}
