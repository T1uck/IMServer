package com.xFly.IMServer.common.user.service.adapter;

import cn.hutool.core.util.RandomUtil;
import com.xFly.IMServer.common.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * 用户适配器
 */
public class UserAdapter {

    /**
     * 构建授权用户
     * @param uid
     * @param userInfo
     */
    public static User buildAuthorizeUser(Long uid, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(uid);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        if (userInfo.getNickname().length() > 6) {
            user.setName("名字过长" + RandomUtil.randomInts(100000));
        }
        else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }
}
