package com.xFly.IMServer.common.user.service;

import com.xFly.IMServer.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xFly.IMServer.common.user.domain.vo.Req.ModifyNameReq;
import com.xFly.IMServer.common.user.domain.vo.Req.WearingBadgeReq;
import com.xFly.IMServer.common.user.domain.vo.Resp.BadgeResp;
import com.xFly.IMServer.common.user.domain.vo.Resp.UserInfoResp;

import java.util.List;

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

    /**
     * 获取用户信息
     * @param uid
     * @return 用户详情
     */
     UserInfoResp getUserInfo(Long uid);

    /**
     * 修改用户名
     * @param uid
     * @param req
     */
    void modifyName(Long uid, ModifyNameReq req);

    /**
     * 获取徽章列表
     * @param uid
     * @return
     */
    List<BadgeResp> badges(Long uid);

    /**
     * 佩戴徽章
     * @param uid
     * @param req
     */
    void wearingBadge(Long uid, WearingBadgeReq req);
}
