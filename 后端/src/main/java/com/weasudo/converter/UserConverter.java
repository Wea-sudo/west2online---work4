package com.weasudo.converter;

import com.weasudo.vo.LoginVO;
import com.weasudo.vo.UserVO;
import com.weasudo.dto.LoginDTO;
import com.weasudo.dto.RegisterDTO;
import com.weasudo.pojo.User;

public class UserConverter {
    private UserConverter() {}

    public static User toEntity(RegisterDTO dto) {
        if(dto == null ) return null;
        User user = new User();
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        return user;
    }

    public static User toEntity(LoginDTO dto) {
        if(dto == null ) return null;
        User user = new User();
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setRole("USER");
        return user;
    }

    public static UserVO ToVO(User user) {
        if(user == null ) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setAvatarUrl(user.getAvatarUrl());
        return vo;
    }

    public  static LoginVO ToLoginVO(User user, String token) {
        if(user == null ) return null;
        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setToken(token);
        return vo;
    }
}
