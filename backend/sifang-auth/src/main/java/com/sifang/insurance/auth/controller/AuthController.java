package com.sifang.insurance.auth.controller;

import com.sifang.insurance.auth.dto.LoginRequest;
import com.sifang.insurance.auth.dto.LoginResponse;
import com.sifang.insurance.auth.entity.User;
import com.sifang.insurance.auth.service.UserService;
import com.sifang.insurance.auth.utils.JwtUtils;
import com.sifang.insurance.common.entity.Result;
import com.sifang.insurance.common.entity.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${jwt.expire}")
    private Integer expire;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        // 根据用户名查询用户
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.fail(ResultCode.FAIL.getCode(), "用户名或密码错误");
        }

        // 生成token
        String accessToken = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setUserType(user.getUserType());
        response.setExpireIn(expire);

        return Result.success(response);
    }
}