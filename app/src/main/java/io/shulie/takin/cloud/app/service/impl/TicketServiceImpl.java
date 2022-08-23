package io.shulie.takin.cloud.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.exceptions.ValidateException;

import io.shulie.takin.cloud.app.service.TicketService;

/**
 * Ticket服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class TicketServiceImpl implements TicketService {
    @javax.annotation.Resource
    StringRedisTemplate stringRedisTemplate;
    private static final String TICKET_CACHE_KEY = "takin:cloud:ticket";
    private static final long TICKET_TIME = DateUnit.MINUTE.getMillis() * 10;
    private static final String TICKET_TIME_SEPARATOR = String.valueOf(CharPool.AT);

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String id) {
        Object ticket = stringRedisTemplate.opsForHash().get(TICKET_CACHE_KEY, id);
        return StrUtil.utf8Str(ticket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generate() {
        return CharSequenceUtil.join(TICKET_TIME_SEPARATOR,
            (System.currentTimeMillis() + TICKET_TIME),
            RandomUtil.randomString(128));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verification(String id, String ticket) {
        try {
            String[] a = CharSequenceUtil.splitToArray(ticket, TICKET_TIME_SEPARATOR);
            if (ArrayUtil.isEmpty(a) || a.length != 2) {
                throw new ValidateException("ticket长度不符:" + ticket);
            }
            String timeString = a[0];
            long time = Long.parseLong(timeString);
            if (time < System.currentTimeMillis()) {
                throw new ValidateException("ticket已过期");
            }
            return true;
        } catch (RuntimeException e) {
            log.error("ticket校验失败", e);
            stringRedisTemplate.opsForHash().delete(TICKET_CACHE_KEY, id);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(String id, String ticket, String publicKey) {
        try {
            stringRedisTemplate.opsForHash().put(TICKET_CACHE_KEY, id, ticket);
            // 如果公钥为空则不加密
            if (!CharSequenceUtil.isNotBlank(publicKey)) {return ticket;}
            // 否则将ticket以公钥加密
            else {
                return SecureUtil.rsa(null, publicKey).encryptBase64(ticket, KeyType.PublicKey);
            }
        } catch (RuntimeException ex) {
            log.error("加密Ticket失败", ex);
            stringRedisTemplate.opsForHash().delete(TICKET_CACHE_KEY, id);
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String sign(byte[] bytes, long timestamp, String ticket) {
        String sb = StrUtil.utf8Str(bytes) + StrUtil.utf8Str(timestamp) + StrUtil.utf8Str(ticket);
        return MD5.create().digestHex(sb);
    }
}
