package io.shulie.takin.cloud.biz.service;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 *
 * @author liuchuan
 * @date 2021/6/2 1:34 下午
 */
public interface DistributedLock {

    /**
     * 尝试获得锁
     *
     * @param lockKey 锁住的 key
     * @param waitTime 等待锁释放的时间
     * @param leaseTime 释放锁的时间
     * @param timeUnit 时间单位
     * @return 是否获得到了锁, true 是, false 否
     */
    boolean tryLock(String lockKey, Long waitTime, Long leaseTime, TimeUnit timeUnit);

    /**
     * 释放锁
     *
     * @param lockKey 锁住的key
     */
    void unLock(String lockKey);

    /**
     * 安全地释放锁
     *
     * 如果是当前线程持有该锁, 就释放, 否则不做
     *
     * @param lockKey 锁key
     */
    void unLockSafely(String lockKey);
}
