package com.xFly.IMServer.common.user.dao;

import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    /**
     * 根据openid获取用户信息
     * @param openid
     */
    public User getByOpenId(String openid) {
        return lambdaQuery().eq(User::getOpenId, openid).one();
    }
}
