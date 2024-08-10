package com.xFly.IMServer.common.user.service.handler;

import com.xFly.IMServer.common.user.service.adapter.TextBuilder;
import com.xFly.IMServer.common.websocket.service.WxMsgService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 扫码事件处理
 */
@Component
public class ScanHandler extends AbstractHandler {

    @Resource
    private WxMsgService wxMsgService;
    
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // 扫码请求
        return wxMsgService.scan(wxMpService,wxMpXmlMessage);
    }

}
