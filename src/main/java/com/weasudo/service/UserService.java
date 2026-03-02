package com.weasudo.service;

import com.weasudo.pojo.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User login(String username, String password);
    Long register(User user);
    boolean updateUser(User user);
    boolean deleteUser(Long id);
}
