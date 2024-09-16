package com.xFly.IMServer.common.websocket.service;

import com.xFly.IMServer.common.websocket.domain.vo.resp.WSBaseResp;
import com.xFly.IMServer.common.websocket.domain.vo.resp.WSBlack;
import io.netty.channel.Channel;

public interface WebSocketService {

    /**
     * 建立websocket链接，将链接放入map中管理
     * @param channel
     */
    void connect(Channel channel);

    /**
     * 处理用户登陆请求，需要返回一张带参数的二维码
     * @param channel
     */
    void handleLoginReq(Channel channel);

    /**
     * 处理ws断开连接的事件
     * @param channel
     */
    void removed(Channel channel);

    /**
     * 扫描二维码将登陆消息返回
     * @param code
     * @param id
     */
    void scanLoginSuccess(Integer code, Long id);

    /**
     * 等待授权信息返回
     * @param code
     */
    void waitAuthorize(Integer code);

    /**
     * 登陆授权
     * @param channel
     * @param token
     */
    void authorize(Channel channel, String token);

    /**
     * 发送消息桂全体用户
     * @param resp 发送的消息体
     * @param skipUid 需要跳过的人
     */
    void sendToAllOnline(WSBaseResp<?> resp, Long skipUid);
}
