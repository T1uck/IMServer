package com.xFly.IMServer.common.common.event.listener;

import com.xFly.IMServer.common.common.event.BlackUserEvent;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.service.cache.UserCache;
import com.xFly.IMServer.common.websocket.domain.enums.WSRespTypeEnum;
import com.xFly.IMServer.common.websocket.domain.vo.resp.WSBaseResp;
import com.xFly.IMServer.common.websocket.domain.vo.resp.WSBlack;
import com.xFly.IMServer.common.websocket.service.WebSocketService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;

/**
 * Description: 拉黑用户事件监听器
 */
public class BlackUserListener {
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private UserCache userCache;
    @Resource
    private UserDao userDao;

    /**
     * 推送拉黑消息
     * @param event 拉黑用户事件
     */
    @Async
    @EventListener(classes = BlackUserEvent.class)
    public void sendMsg(BlackUserEvent event) {
        Long uid = event.getUser().getId();
        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
        WSBlack wsBlack = new WSBlack(uid);
        resp.setData(wsBlack);
        resp.setType(WSRespTypeEnum.BLACK.getType());
        // 推送
        webSocketService.sendToAllOnline(resp,uid);
    }

    /**
     * 刷新缓存
     * @param event 拉黑用户事件
     */
    @Async
    @EventListener(classes = BlackUserEvent.class)
    public void refreshRedis(BlackUserEvent event) {
        userCache.evictBlackMap();
    }

    /**
     * 修改用户状态
     * @param event 拉黑用户事件
     */
    @Async
    @EventListener(classes = BlackUserEvent.class)
    public void changeStatus(BlackUserEvent event) {
        userDao.invalidUser(event.getUser().getId());
    }
}
