package com.weasudo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weasudo.exception.BusinessException;
import com.weasudo.converter.UserConverter;
import com.weasudo.dto.LoginDTO;
import com.weasudo.dto.RegisterDTO;
import com.weasudo.dto.UpdateUserInfoDTO;
import com.weasudo.dto.UpdateUserPasswordDTO;
import com.weasudo.mapper.UserMapper;
import com.weasudo.pojo.User;
import com.weasudo.service.UserService;
import com.weasudo.util.JwtUtil;
import com.weasudo.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.weasudo.common.ResponseCodeEnum.*;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers(){
        return this.list();
    }

    @Override
    public User getUserById(String id) {
        return this.getById(id);
    }


    @Override
    public void register(RegisterDTO dto) {

        // 1. 判断密码是否一致
        if(!dto.getPassword().equals(dto.getConfirmPassword())) {
            log.warn("两次密码不一致: {}", dto.getName());
            throw new BusinessException(PASSWORD_NOT_MATCH);
        }


        User user = UserConverter.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setId(IdWorker.getIdStr());


        Long count = this.lambdaQuery()
                .eq(User::getName, dto.getName())
                .count();
        if(count > 0) {
            log.warn("用户名已存在: {}", dto.getName());
            throw new BusinessException(USERNAME_ALREADY_EXIST);
        }
        // 未保存成功，已有用户名
        try{
            this.save(user);
        } catch (DuplicateKeyException e) {
            log.warn("用户名已存在: {}", dto.getName());
            throw new BusinessException(USERNAME_ALREADY_EXIST);
        }


        log.info("用户注册成功: {}", dto.getName());

    }

    @Override
    public LoginVO login(LoginDTO dto) {

        User user = this.lambdaQuery()
                .eq(User::getName,dto.getName())
                .one();
        if (user == null) {
            log.warn("用户不存在: {}", dto.getName());
            throw new BusinessException(USER_NOT_EXIST);
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("密码错误: {}", dto.getName());
            throw new BusinessException(PASSWORD_ERROR);
        }

        String token = JwtUtil.generateToken(user.getId(),user.getRole());
        System.out.println("生成的Token: " + token);
        if(token == null) {
            log.warn("生成Token失败: {}", dto.getName());
            throw new BusinessException(GENERATE_TOKEN_FAILED);
        }
        log.info("生成Token成功: {}", dto.getName());

        LoginVO vo = UserConverter.ToLoginVO(user, token);
        log.info("用户登录成功: {}", dto.getName());
        return vo;
    }

    @Override
    public void updateUserPassword(String id, UpdateUserPasswordDTO dto){
        if(!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            log.warn("两次密码不一致: id={}", id);
            throw new BusinessException(PASSWORD_NOT_MATCH);
        }

        User user = this.getById(id);
        if(user == null) {
            log.warn("用户不存在: id={}", id);
            throw new BusinessException(USER_NOT_EXIST);
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            log.warn("旧密码错误: id={}", id);
            throw new BusinessException(OLD_PASSWORD_ERROR);
        }

        if(passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            log.warn("新密码不能与旧密码相同: id={}", id);
            throw new BusinessException(NEW_PASSWORD_SAME_AS_OLD);
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        try{
            this.updateById(user);
        } catch (DuplicateKeyException e) {
            log.warn("更新用户密码失败: id={}", id);
            throw new BusinessException(UPDATE_USER_PASSWORD_FAILED);
        }
    }

    @Override
    public void updateUserInfo(String id, UpdateUserInfoDTO dto){
        User user = this.getById(id);
        if(user == null) {
            log.warn("用户不存在: id={}", id);
            throw new BusinessException(USER_NOT_EXIST);
        }
        User existUser = this.lambdaQuery()
                .eq(User::getName, dto.getName())
                .one();
        if(existUser !=null && !existUser.getId().equals(id)) {
            log.warn("用户名已存在: {}", dto.getName());
            throw new BusinessException(USERNAME_ALREADY_EXIST);
        }
        user.setName(dto.getName());
        user.setAvatarUrl(dto.getAvatarUrl());
        try{
            this.updateById(user);
        } catch (DuplicateKeyException e) {
            log.warn("更新用户信息失败: id={}", id);
            throw new BusinessException(UPDATE_USER_INFO_FAILED);
        }
        log.info("更新用户信息成功: id={}", id);
    }


    @Override
    public void updateUser(User user) {

        try{
            this.updateById(user);
        } catch (DuplicateKeyException e) {
            log.warn("更新用户失败: {}", user.getName());
            throw new BusinessException(UPDATE_USER_FAILED);
        }

        log.info("更新用户成功: {}", user.getName());
    }

    @Override
    public void deleteUser(String id) {

        try{
            this.removeById(id);
        } catch (DuplicateKeyException e) {
            log.warn("删除用户失败: id={}", id);
            throw new BusinessException(DELETE_USER_FAILED);
        }

        log.info("删除用户成功: id={}", id);
    }
}
