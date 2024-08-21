package com.xFly.IMServer.common.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.xFly.IMServer.common.common.annotation.RedissonLock;
import com.xFly.IMServer.common.common.service.LockService;
import com.xFly.IMServer.common.common.utils.SpElUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Description: 分布式锁切面
 */
@Component
@Aspect
@Order(0) // 确保分布式锁比事物注解先执行，分布式锁在事物外
public class RedissonAspect {
    @Resource
    private LockService lockService;

    @Around("@annotation(com.xFly.IMServer.common.common.annotation.RedissonLock)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
        String prefix = StrUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();//默认方法限定名+注解排名（可能多个）
        String key = SpElUtils.parseSpEl(method, proceedingJoinPoint.getArgs(), redissonLock.key());
        return lockService.executeWithLockThrows(prefix + ":" + key, redissonLock.waitTime(), redissonLock.unit(), proceedingJoinPoint::proceed);
    }
}
