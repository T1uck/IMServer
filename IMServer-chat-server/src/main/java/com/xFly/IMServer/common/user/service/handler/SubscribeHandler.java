package com.xFly.IMServer.common.user.service.handler;

import com.xFly.IMServer.common.user.service.adapter.TextBuilder;
import com.xFly.IMServer.common.websocket.service.WxMsgService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/*
 * 关注事件处理
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Resource
    private WxMsgService wxMsgService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());
//        // 获取微信用户基本信息
//        try {
//            WxMpUser userWxInfo = wxMpService.getUserService()
//                    .userInfo(wxMessage.getFromUser(), null);
//            if (userWxInfo != null) {
//                // TODO 可以添加关注用户到本地数据库
//            }
//        } catch (WxErrorException e) {
//            if (e.getError().getErrorCode() == 48001) {
//                this.logger.info("该公众号没有获取用户信息权限！");
//            }
//        }

        WxMpXmlOutMessage responseResult = null;
        try {
            // 进入登陆请求
            responseResult = this.handleSpecial(wxMpService, wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        if (responseResult != null) {
            return responseResult;
        }

        try {
            return new TextBuilder().build("感谢关注", wxMessage, wxMpService);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpService wxMpService, WxMpXmlMessage wxMessage)
            throws Exception {
        return wxMsgService.scan(wxMpService,wxMessage);
    }

}
