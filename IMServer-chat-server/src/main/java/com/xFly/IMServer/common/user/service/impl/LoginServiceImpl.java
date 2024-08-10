package com.xFly.IMServer.common.user.service.impl;


import cn.hutool.core.util.StrUtil;
import com.xFly.IMServer.common.common.constant.RedisKey;
import com.xFly.IMServer.common.common.utils.JwtUtils;
import com.xFly.IMServer.common.common.utils.RedisUtils;
import com.xFly.IMServer.common.user.service.LoginService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    public static final int TOKEN_EXPIRE_DAYS = 5;
    public static final int TOKEN_RENEWAL_DAYS = 2;
    @Resource
    private JwtUtils jwtUtils;

    /**
     * 校验token是不是有效
     *
     * @param token
     * @return
     */
    @Override
    public boolean verify(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return false;
        }
        String key = getKey(uid);
        String relToken = RedisUtils.getStr(key);
        // 校验是不是和最新的token一致
        return Objects.equals(token, relToken);
    }

    /**
     * 刷新token有效期
     *
     * @param token
     */
    @Async
    @Override
    public void renewalTokenIfNecessary(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return;
        }
        String key = getKey(uid);
        Long expireTime = RedisUtils.getExpire(key);
        // 不存在
        if (expireTime == -2) {
            return;
        }
        if (expireTime < TOKEN_RENEWAL_DAYS) {
            RedisUtils.expire(key, TOKEN_EXPIRE_DAYS,TimeUnit.DAYS);
        }
    }

    /**
     * 登录成功，获取token
     *
     * @param id
     * @return 返回token
     */
    @Override
    public String login(Long id) {
        String key = getKey(id);
        String token = RedisUtils.getStr(key);
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        token = jwtUtils.createToken(id);
        RedisUtils.set(getKey(id), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 获取key
     * @param id
     * @return
     */
    private static String getKey(Long id) {
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, id);
    }

    /**
     * 如果token有效，返回uid
     *
     * @param token
     * @return
     */
    @Override
    public Long getValidUid(String token) {
        boolean verify = verify(token);
        return verify ? jwtUtils.getUidOrNull(token) : null;
    }
}
