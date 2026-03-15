package com.weasudo.controller;


import com.weasudo.dto.UpdateUserInfoDTO;
import com.weasudo.dto.UpdateUserPasswordDTO;
import com.weasudo.util.JwtUtil;
import com.weasudo.vo.LoginVO;
import com.weasudo.vo.UserVO;
import com.weasudo.converter.UserConverter;
import com.weasudo.dto.LoginDTO;
import com.weasudo.dto.RegisterDTO;
import com.weasudo.common.Response;
import com.weasudo.pojo.User;
import com.weasudo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public Response<Void> register(@RequestBody RegisterDTO dto) {
        log.info("用户注册请求: {}", dto.getName());
        userService.register(dto);
        return Response.success();
    }

    @PostMapping("/login")
    public Response<LoginVO> login(@RequestBody LoginDTO dto) {
        log.info("用户登录请求: {}", dto.getName());
        LoginVO vo = userService.login(dto);
        return Response.success(vo);
    }

    @PutMapping("/password")
    public Response<Void> updatePassword(@RequestBody UpdateUserPasswordDTO dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = JwtUtil.getUserId(token);
        log.info("更新用户密码请求: id={}", userId);
        userService.updateUserPassword(userId,dto);
        return Response.success();
    }

    @PutMapping("/info")
    public Response<Void> updateProfile(@RequestBody UpdateUserInfoDTO dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.info("更新用户信息请求: token={}", token);
        String userId = JwtUtil.getUserId(token);
        log.info("更新用户信息请求: id={}", userId);
        userService.updateUserInfo(userId, dto);
        return Response.success();
    }


    @GetMapping("/{id}")
    public Response<UserVO> getUserById(@PathVariable String id) {
        log.info("获取用户信息请求: id={}", id);
        User user = userService.getUserById(id);
        UserVO vo = UserConverter.ToVO(user);
        System.out.println(vo);
        return Response.success(vo);
    }

    @GetMapping("/me")
    public Response<UserVO> getUserByToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String id = JwtUtil.getUserId(token);
        log.info("根据 token 获取用户信息请求: id={}", id);

        User user = userService.getUserById(id);
        UserVO vo = UserConverter.ToVO(user);
        return Response.success(vo);
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteUser(@PathVariable String id) {
        log.info("删除用户请求: id={}", id);
        userService.deleteUser(id);
        return Response.success();
    }




}

