package com.xFly.IMServer.common.common.event;

import com.xFly.IMServer.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Description: 用户注册事件
 */
@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private final User user;
    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
