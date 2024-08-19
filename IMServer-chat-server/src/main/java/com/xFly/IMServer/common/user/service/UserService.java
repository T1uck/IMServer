package com.xFly.IMServer.common.user.service;

import com.xFly.IMServer.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xFly.IMServer.common.user.domain.vo.Req.ModifyNameReq;
import com.xFly.IMServer.common.user.domain.vo.Resp.UserInfoResp;

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
}
