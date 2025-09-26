package com.sifang.insurance.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sifang.insurance.auth.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
    
    /**
     * 根据手机号查询用户
     */
    User findByPhone(String phone);
    
    /**
     * 用户注册
     */
    boolean register(User user);
    
    /**
     * 更新用户状态
     */
    boolean updateStatus(Long id, Integer status);
}