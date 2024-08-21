package com.xFly.IMServer.common.common.service;

import com.xFly.IMServer.common.common.exception.BusinessException;
import com.xFly.IMServer.common.common.exception.CommonErrorEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Description: 锁服务
 */
@Service
@Slf4j
public class LockService {

    @Resource
    private RedissonClient redissonClient;

    public <T> T executeWithLockThrows(String key, int waitTime, TimeUnit unit, SupplierThrows<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        boolean success = lock.tryLock(waitTime, unit);
        if (!success) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get(); // 执行锁内代码逻辑
        }
        finally {
            lock.unlock();
        }
    }

    @SneakyThrows
    public<T> T executeWithLock(String key, int waitTime, TimeUnit unit, Supplier<T> supplier) {
        return executeWithLockThrows(key, waitTime, unit, supplier::get);
    }

    public <T> T executeWithLock(String key, Supplier<T> supplier) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, supplier);
    }

    @FunctionalInterface
    public interface SupplierThrows<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }
}
