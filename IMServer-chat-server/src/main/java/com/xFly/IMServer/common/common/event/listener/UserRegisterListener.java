package com.xFly.IMServer.common.common.event.listener;

import com.xFly.IMServer.common.common.domain.enums.IdempotentEnum;
import com.xFly.IMServer.common.common.event.UserRegisterEvent;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.domain.enums.ItemEnum;
import com.xFly.IMServer.common.user.service.IUserBackpackService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;

/**
 * Description: 用户注册监听
 */
public class UserRegisterListener {
    @Resource
    private UserDao userDao;
    @Resource
    private IUserBackpackService userBackpackService;

    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        int count = userDao.count();// todo: 性能瓶颈，等注册用户多了直接删掉
        if (count <= 10) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (count <= 100) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }
    }
}
