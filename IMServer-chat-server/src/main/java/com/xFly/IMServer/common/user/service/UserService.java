package com.xFly.IMServer.common.user.service;

import com.xFly.IMServer.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册，需要获得id
     * @param user
     */
    void register(User user);
}
