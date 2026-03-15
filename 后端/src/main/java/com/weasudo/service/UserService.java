package com.weasudo.service;

import com.weasudo.dto.LoginDTO;
import com.weasudo.dto.RegisterDTO;
import com.weasudo.dto.UpdateUserPasswordDTO;
import com.weasudo.dto.UpdateUserInfoDTO;
import com.weasudo.pojo.User;
import com.weasudo.vo.LoginVO;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(String id);
    LoginVO login(LoginDTO dto);
    void register(RegisterDTO registerDTO);
    void updateUserPassword(String id, UpdateUserPasswordDTO dto);
    void updateUserInfo(String id, UpdateUserInfoDTO dto);
    void updateUser(User user);
    void deleteUser(String id);
}
