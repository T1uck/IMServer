package com.xFly.IMServer.common.user.domain.entity;

import jodd.util.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Description: 用户IP信息
 */
@Data
public class IpInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    // 注册时的IP
    private String createIp;
    // 注册时的IP详情
    private IpDetail createIpDetail;
    // 最新登陆的IP
    private String updateIp;
    // 最新登陆的IP详情
    private IpDetail updateIpDetail;

    /**
     * 需要刷新的IP，这里判断更新的ip就够，初始化的时候ip也是相同的，只需要设置的时候多设置进去就行
     */
    public String needRefreshIp() {
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(ip, updateIp))
                .isPresent();
        return notNeedRefresh ? null : updateIp;
    }

    /**
     * 刷新ip详情
     * @param ipDetail ip详情
     */
    public void refreshIpDetail(IpDetail ipDetail) {
        // 如果创建ip与ip详情中相同
        if (Objects.equals(createIp,ipDetail.getIp())) {
            createIpDetail = ipDetail;
        }
        // 如果更新ip与ip详情中相同
        if (Objects.equals(updateIp,ipDetail.getIp())) {
            updateIpDetail = ipDetail;
        }
    }

    /**
     * 刷新ip
     * @param ip 当前登陆ip
     */
    public void refreshIp(String ip) {
        if (StringUtil.isEmpty(ip)) {
            return;
        }
        // 如果创建ip与ip详情中相同
        if (Objects.equals(createIp,ip)) {
            createIp = ip;
        }
    }
}

