package com.xFly.IMServer.common.common.event;

import com.xFly.IMServer.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Description: 用户上线事件
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private final User user;

    public UserOnlineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
