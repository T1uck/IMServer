package com.xFly.IMServer.common.user.domain.entity;

import lombok.*;

import java.io.Serializable;

/**
 * Description: 用户IP详情
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    // 注册时的ip
    private String ip;
    // 运营商
    private String isp;
    // 运营商id
    private String isp_id;
    // 国家
    private String city;
    // 国家id
    private String city_id;
    // 省份
    private String country;
    // 省份id
    private String country_id;
    // 城市
    private String region;
    // 城市id
    private String region_id;
}
