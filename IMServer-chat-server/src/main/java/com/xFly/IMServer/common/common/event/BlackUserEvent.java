package com.xFly.IMServer.common.common.event;

import com.xFly.IMServer.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Description: 用户拉黑事件
 */
@Getter
public class BlackUserEvent extends ApplicationEvent {
    private final User user;

    public BlackUserEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
