package com.xFly.IMServer.common.user.dao;

import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.mapper.user.UserMapper;
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

    /**
     * 根据用户名获取用户信息
     * @param newName
     */
    public User getByName(String newName) {
        return lambdaQuery().eq(User::getName, newName).one();
    }

    /**
     * 修改用户名
     * @param uid
     * @param newName
     */
    public void modifyName(Long uid, String newName) {
        lambdaUpdate().eq(User::getId, uid)
                .set(User::getName, newName)
                .update();
    }
}
