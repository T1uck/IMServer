package com.xFly.IMServer.common.websocket.service;


import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public interface WxMsgService {

    /**
     * 扫描二维码
     * @param wxMpService
     * @param wxMessage
     * @return
     */
    WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMessage);

    /**
     * 用户授权
     * @param userInfo
     */
    void authorize(WxOAuth2UserInfo userInfo);
}
