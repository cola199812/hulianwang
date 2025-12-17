package com.outdoor.demo.service;

import com.outdoor.demo.entity.User;
import com.outdoor.demo.entity.UserRegisterRequest;

public interface UserService {
    User register(UserRegisterRequest req);
    User login(String username, String password);
    User getById(Long id);
    void resetPassword(String email, String code, String newPassword);
}

