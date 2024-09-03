package com.xFly.IMServer.common.user.service;

/**
 * Description: ip服务
 */
public interface IpService {
    /**
     * 异步更新用户ip详情
     * @param uid 用户id
     */
    void refreshIpDetailAsync(Long uid);
}
