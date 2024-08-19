package com.xFly.IMServer.common.user.service.adapter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.xFly.IMServer.common.common.domain.enums.YesOrNoEnum;
import com.xFly.IMServer.common.user.domain.entity.ItemConfig;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.domain.entity.UserBackpack;
import com.xFly.IMServer.common.user.domain.vo.Resp.BadgeResp;
import com.xFly.IMServer.common.user.domain.vo.Resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户适配器
 */
public class UserAdapter {

    /**
     * 构建授权用户
     *
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
        } else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }

    /**
     * 构建用户信息返回
     *
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

    /**
     * 用户徽章返回
     *
     * @param itemConfigList
     * @param backpacks
     * @param user
     * @return
     */
    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigList, List<UserBackpack> backpacks, User user) {
        if (ObjectUtil.isNull(user)) {
            // 这里 user 入参可能为空
            return Collections.emptyList();
        }
        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigList.stream().map(a -> {
                    BadgeResp badgeResp = new BadgeResp();
                    BeanUtils.copyProperties(a, badgeResp);
                    badgeResp.setObtain(obtainItemSet.contains(a.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    badgeResp.setWearing(ObjectUtil.equal(a.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    return badgeResp;
                }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                        .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
