package com.xFly.IMServer.common.user.dao;

import com.xFly.IMServer.common.common.domain.enums.YesOrNoEnum;
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
     * @param openid 微信openid
     */
    public User getByOpenId(String openid) {
        return lambdaQuery().eq(User::getOpenId, openid).one();
    }

    /**
     * 根据用户名获取用户信息
     * @param newName 用户名
     */
    public User getByName(String newName) {
        return lambdaQuery().eq(User::getName, newName).one();
    }

    /**
     * 修改用户名
     * @param uid 用户id
     * @param newName 新用户名
     */
    public void modifyName(Long uid, String newName) {
        lambdaUpdate().eq(User::getId, uid)
                .set(User::getName, newName)
                .update();
    }

    /**
     * 佩戴徽章
     * @param uid 用户id
     * @param badgeId 背包id
     */
    public void wearingBadge(Long uid, Long badgeId) {
        lambdaUpdate().eq(User::getId, uid)
                .set(User::getItemId,badgeId)
                .update();
    }

    /**
     * 用户拉黑
     * @param id 用户id
     */
    public void invalidUser(Long id) {
        lambdaUpdate().eq(User::getId, id)
                .set(User::getStatus, YesOrNoEnum.YES.getStatus())
                .update();
    }
}
