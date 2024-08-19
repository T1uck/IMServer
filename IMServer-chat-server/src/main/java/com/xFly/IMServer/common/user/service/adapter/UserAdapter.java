package com.xFly.IMServer.common.user.service.adapter;

import cn.hutool.core.util.RandomUtil;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.domain.vo.Resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.BeanUtils;

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

    /**
     * 构建用户信息返回
     * @param user
     * @param countByValidItemId
     * @return
     */
    public static UserInfoResp buildUserInfoResp(User user, Integer countByValidItemId) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtils.copyProperties(user, userInfoResp);
        userInfoResp.setModifyNameChance(countByValidItemId);
        return userInfoResp;
    }
}
