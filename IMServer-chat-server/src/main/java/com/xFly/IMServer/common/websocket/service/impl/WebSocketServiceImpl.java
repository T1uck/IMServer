package com.xFly.IMServer.common.websocket.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.service.LoginService;
import com.xFly.IMServer.common.websocket.domain.dto.WSChannelExtraDTO;
import com.xFly.IMServer.common.websocket.domain.enums.WSRespTypeEnum;
import com.xFly.IMServer.common.websocket.domain.vo.resp.WSBaseResp;
import com.xFly.IMServer.common.websocket.domain.vo.resp.WSLoginUrl;
import com.xFly.IMServer.common.websocket.service.WebSocketService;
import com.xFly.IMServer.common.websocket.service.adapter.WSAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Resource
    @Lazy
    private WxMpService wxMpService;

    @Resource
    private UserDao userDao;

    @Resource
    private LoginService loginService;

    /**
     * 所有已连接的websocket链接列表和一些额外参数
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    public static final Duration EXPIRE_TIME = Duration.ofHours(1);
    public static final long MAX_NUM_SIZE = 10000L;

    /**
     * 所有请求登陆的code与channel的关系
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_NUM_SIZE)
            .build();

    /**
     * 建立websocket链接，将链接放入map中管理
     *
     * @param channel
     */
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 处理用户登陆请求，需要返回一张带参数的二维码
     *
     * @param channel
     */
    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        // 生成随机二维码
        Integer code = generateLoginCode(channel);
        // 找微信申请带参二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) MAX_NUM_SIZE);
        // 把码推送给前端 (channel 在本地）
        WSBaseResp<WSLoginUrl> resp = WSAdapter.buildLoginUrl(wxMpQrCodeTicket);
        sendMsg(channel, resp);
    }

    /**
     * 处理ws断开连接的事件
     * @param channel
     */
    @Override
    public void removed(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        // todo: 用户下线
    }

    /**
     * 扫描二维码将登陆消息返回
     * @param code
     * @param id
     */
    @Override
    public void scanLoginSuccess(Integer code, Long id) {
        // 确认链接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (ObjectUtil.isNull(channel)) {
            return;
        }
        User user = userDao.getById(id);
        // 移除code
        WAIT_LOGIN_MAP.invalidate(code);
        // 调用用户登陆模块,获取token
        String token = loginService.login(id);
        // 发送登陆信息
        loginSuccessMsg(channel, token, user);
    }

    /**
     * 等待授权信息返回
     * @param code
     */
    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (ObjectUtil.isNull(channel)) {
            return;
        }
        sendMsg(channel, WSAdapter.buildWaitAuthorizeSuccessResp());
    }

    /**
     * 登陆授权
     * @param channel
     * @param token
     */
    @Override
    public void authorize(Channel channel, String token) {
        Long uid = loginService.getValidUid(token);
        if (Objects.nonNull(uid)) {
            User user = userDao.getById(uid);
            loginSuccessMsg(channel, token, user);
        }else {
            sendMsg(channel, WSAdapter.buildInvalidTokenResp());
        }
    }


    /**
     * 推送登陆成功消息
     * @param channel
     * @param token
     * @param user
     */
    private void loginSuccessMsg(Channel channel, String token, User user) {
        // 保存channel对应uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        // todo: 用户上线成功的事件
        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user, token));
    }

    /**
     * 给本地channel发送消息
     *
     * @param channel
     * @param resp
     */
    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    /**
     * 获取不重复的登陆的code,微信要求最大不超过int的存储极限
     *
     * @param channel
     */
    private Integer generateLoginCode(Channel channel) {
        int code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (ObjectUtil.isNotNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }
}
