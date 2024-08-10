package com.xFly.IMServer.common.websocket.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.service.UserService;
import com.xFly.IMServer.common.user.service.adapter.TextBuilder;
import com.xFly.IMServer.common.user.service.adapter.UserAdapter;
import com.xFly.IMServer.common.websocket.service.WebSocketService;
import com.xFly.IMServer.common.websocket.service.WxMsgService;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WxMsgServiceImpl implements WxMsgService {

    /**
     * 用于存储openid 和 登陆code 的map
     */
    private final static ConcurrentHashMap<String,Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

    private final static String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Value("${wx.mp.callback}")
    private String callback;

    @Resource
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Resource
    private WebSocketService webSocketService;

    /**
     * 扫描二维码
     * @param wxMpService
     * @param wxMessage
     * @return
     */
    @Override
    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMessage) {
        String openid = wxMessage.getFromUser();
        Integer code = Integer.parseInt(getEventKey(wxMessage));
        User user = userDao.getByOpenId(openid);
        if (ObjectUtil.isNull(code)) {
            return null;
        }
        // 如果已经注册直接登陆成功
        if (ObjectUtil.isNotNull(user) && StringUtils.isNotEmpty(user.getAvatar())) {
            // 推送登陆信息
            webSocketService.scanLoginSuccess(code,user.getId());
        }
        // user为空，手动生成，以保存uid
        if (ObjectUtil.isNull(user)) {
            user = User.builder().openId(openid).build();
            userService.register(user);
        }
        // 将openid 和 登陆code存入map中
        WAIT_AUTHORIZE_MAP.put(openid, code);
        // 推送授权信息
        webSocketService.waitAuthorize(code);
        // 需要注入的两个值，一是用户的appid也是公众号的唯一标识，二是授权后的重定向回调地址
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击链接授权：<a href=\"" + authorizeUrl + "\">登录</a>", wxMessage, wxMpService);
    }

    /**
     *  用户授权
     * @param userInfo
     */
    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        User user = userDao.getByOpenId(userInfo.getOpenid());
        // 更新用户信息
        if (StringUtils.isEmpty(user.getAvatar())) {
            fillUserInfo(user.getId(),userInfo);
        }
        // 找到对应的 code对应的 channel进行登陆
        Integer code = WAIT_AUTHORIZE_MAP.remove(userInfo.getOpenid());
        // 发送登陆信息到前端
        webSocketService.scanLoginSuccess(code,user.getId());
    }

    /**
     * 补充用户信息
     * @param uid
     * @param userInfo
     */
    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(uid, userInfo);
        userDao.updateById(user);
    }

    private String getEventKey(WxMpXmlMessage wxMessage) {
        return wxMessage.getEventKey().replace("qrscene_", "");
    }
}
