package com.xFly.IMServer.common.websocket.service.adapter;

import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.websocket.domain.enums.WSRespTypeEnum;
import com.xFly.IMServer.common.websocket.domain.vo.resp.WSBaseResp;
import com.xFly.IMServer.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.xFly.IMServer.common.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * ws 消息适配器
 */
public class WSAdapter {

    /**
     * 创建登陆返回信息
     * @param wxMpQrCodeTicket
     * @return
     */
    public static WSBaseResp<WSLoginUrl> buildLoginUrl(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    /**
     * 登陆成功返回信息
     * @param user
     * @param token
     * @return
     */
    public static WSBaseResp<?> buildLoginSuccessResp(User user, String token, Boolean hasPower) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .power(hasPower ? 1 : 0)
                .build();
        resp.setData(wsLoginSuccess);
        return resp;
    }

    /**
     * 等待授权返回信息
     * @return
     */
    public static WSBaseResp<?> buildWaitAuthorizeSuccessResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    /**
     * 校验token有效期
     * @return
     */
    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }
}
