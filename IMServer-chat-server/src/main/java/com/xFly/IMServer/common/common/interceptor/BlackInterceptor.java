package com.xFly.IMServer.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.xFly.IMServer.common.common.domain.dto.RequestInfo;
import com.xFly.IMServer.common.common.exception.HttpErrorEnum;
import com.xFly.IMServer.common.common.utils.RequestHolder;
import com.xFly.IMServer.common.user.domain.enums.BlackTypeEnum;
import com.xFly.IMServer.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 黑名单拦截器
 */
@Order(2)
@Slf4j
@Component
public class BlackInterceptor implements HandlerInterceptor {
    @Resource
    private UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        RequestInfo requestInfo = RequestHolder.get();
        // ip拉黑
        if (isBlackList(requestInfo.getIp(), blackMap.get(BlackTypeEnum.IP.getType()))) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        // uid拉黑
        if (isBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()))) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    /**
     * 判断是否在黑名单中
     * @param target 拉黑目标
     * @param blackSet 拉黑名单
     * @return
     */
    private boolean isBlackList(Object target, Set<String> blackSet) {
        if (Objects.isNull(target) || Objects.isNull(blackSet)) {
            return false;
        }
        return blackSet.contains(target.toString());
    }
}
