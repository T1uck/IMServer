package com.xFly.IMServer.common.common.interceptor;

import com.xFly.IMServer.common.common.exception.HttpErrorEnum;
import com.xFly.IMServer.common.user.service.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * 用户鉴权拦截器
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Resource
    private LoginService loginService;

    public static final String ATTRIBUTE_UID = "uid";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取登陆用户token
        String token = getToken(request);
        // 校验token获取uid
        Long uid = loginService.getValidUid(token);
        if (Objects.nonNull(uid)) {
            // 有登陆态
            request.setAttribute(ATTRIBUTE_UID, uid);
        }
        else {
            boolean publicURI = isPublicURI(request);
            if (!publicURI){
                // 没有登陆态,又不是公开路径，直接401
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;
    }

    private static boolean isPublicURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        return split.length > 2 && "public".equals(split[3]);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.substring(AUTHORIZATION_SCHEMA.length()))
                .orElse(null);
    }
}
