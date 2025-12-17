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
public class UserController {
    private final UserService userService;
    private final com.outdoor.demo.service.VerificationCodeService verificationCodeService;

    public UserController(UserService userService, com.outdoor.demo.service.VerificationCodeService verificationCodeService) {
        this.userService = userService;
        this.verificationCodeService = verificationCodeService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        verificationCodeService.sendCode(email);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "验证码已发送（请查看控制台）");
        return ResponseEntity.ok(body);
    }

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

    // 处理参数校验异常（如邮箱格式错误、为空等）
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        // 获取第一个错误信息
        String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        body.put("message", errorMsg);
        return ResponseEntity.badRequest().body(body);
    }

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
        body.put("createTime", u.getCreateTime());
        return ResponseEntity.ok(body);
    }
}

