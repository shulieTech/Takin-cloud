package io.shulie.takin.cloud.biz.service;

import java.util.concurrent.TimeUnit;

import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author liuchuan
 * @date 2021/6/2 1:35 下午
 */
@Service
public class RedissonDistributedLock implements DistributedLock {

    @Qualifier("redisson")
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean tryLock(String lockKey, Long waitTime, Long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.MIDDLEWARE_JAR_GET_LOCK_ERROR, "尝试获得锁失败!", e);
        }
    }

    @Override
    public void unLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    @Override
    public void unLockSafely(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

}
