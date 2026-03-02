package com.weasudo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weasudo.mapper.UserMapper;
import com.weasudo.pojo.User;
import com.weasudo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers(){
        return this.list();
    }

    @Override
    public User getUserById(Long id) {
        return this.getById(id);
    }

    @Override
    public User login(String username, String password) {
        User user = this.lambdaQuery()
                .eq(User::getName,username)
                .one();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }


    @Override
    public Long register(User user) {
        Long count = this.lambdaQuery()
                .eq(User::getName,user.getName())
                .count();
        if(count > 0){
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.save(user);
        return user.getId();
    }

    @Override
    public boolean updateUser(User user) {
        return this.updateById(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return this.removeById(id);
    }
}
