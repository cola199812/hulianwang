package com.outdoor.demo.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.outdoor.demo.entity.User;
import com.outdoor.demo.entity.UserRegisterRequest;
import com.outdoor.demo.mapper.UserMapper;
import com.outdoor.demo.service.UserService;

@Service
/**
 * 用户服务实现类
 * 实现用户注册、登录、信息管理等业务逻辑。
 */
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final com.outdoor.demo.service.VerificationCodeService verificationCodeService;

    public UserServiceImpl(UserMapper userMapper, com.outdoor.demo.service.VerificationCodeService verificationCodeService) {
        this.userMapper = userMapper;
        this.verificationCodeService = verificationCodeService;
    }

    @Override
    /**
     * 用户注册
     * 校验验证码、用户名/邮箱唯一性，加密密码后保存用户。
     */
    public User register(UserRegisterRequest req) {
        if (!verificationCodeService.verifyCode(req.getEmail(), req.getVerificationCode())) {
            throw new RuntimeException("验证码无效或已过期");
        }
        if (userMapper.findByUsername(req.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.findByUsernameOrPhoneOrEmail(req.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    @Override
    /**
     * 用户登录
     * 验证用户名和密码。
     */
    public User login(String username, String password) {
        System.out.println("Login attempt for username: " + username);
        User user = userMapper.findByUsernameOrPhoneOrEmail(username);
        if (user != null) {
            System.out.println("User found: " + user.getUsername());
            System.out.println("Stored password: " + user.getPassword());
            System.out.println("Password match: " + encoder.matches(password, user.getPassword()));
            if (encoder.matches(password, user.getPassword())) {
                return user;
            }
        } else {
            System.out.println("User not found");
        }
        return null;
    }

    @Override
    /**
     * 根据ID获取用户
     */
    public User getById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    /**
     * 重置密码
     * 校验验证码，更新用户密码。
     */
    public void resetPassword(String email, String code, String newPassword) {
        if (!verificationCodeService.verifyCode(email, code)) {
            throw new RuntimeException("验证码无效或已过期");
        }
        User user = userMapper.findByUsernameOrPhoneOrEmail(email);
        if (user == null) {
            throw new RuntimeException("该邮箱未注册");
        }
        userMapper.updatePassword(email, encoder.encode(newPassword));
    }

    @Override
    /**
     * 更新用户个人信息
     * 更新用户的昵称、头像、性别、生日、简介和手机号。
     */
    public User updateUserProfile(User user) {
        userMapper.updateUserProfile(user);
        return userMapper.findById(user.getId());
    }
}

