package com.xFly.IMServer.common.common.event.listener;

import com.xFly.IMServer.common.common.event.UserOnlineEvent;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.domain.enums.ChatActiveStatusEnum;
import com.xFly.IMServer.common.user.service.IpService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

/**
 * Description: 用户上线监听
 */
public class UserOnlineListener {
    @Resource
    private UserDao userDao;

    @Resource
    private IpService ipService;

    @Async
    @EventListener(classes = UserOnlineListener.class)
    public void saveDB(UserOnlineEvent event) {
        // 保存用户上线信息到数据库
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        // 更新用户详情
        ipService.refreshIpDetailAsync(user.getId());
    }
}
