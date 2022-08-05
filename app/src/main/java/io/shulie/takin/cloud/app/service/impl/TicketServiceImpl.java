package io.shulie.takin.cloud.app.service.impl;

import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.asymmetric.RSA;
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

    ConcurrentHashMap<String, String> ticketMap = new ConcurrentHashMap<>();
    private static final long TICKET_TIME = DateUnit.MINUTE.getMillis() * 10;
    private static final String TICKET_TIME_SEPARATOR = String.valueOf(CharPool.AT);

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String id) {
        String ticket = ticketMap.get(id);
        return ticket + "";
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
    public boolean verification(String ticket) {
        try {
            String[] a = CharSequenceUtil.splitToArray(ticket, TICKET_TIME_SEPARATOR);
            if (ArrayUtil.isEmpty(a) || a.length != 2) {
                throw new ValidateException("ticket长度不符");
            }
            String timeString = a[0];
            long time = Long.parseLong(timeString);
            if (time < System.currentTimeMillis()) {
                throw new ValidateException("ticket已过期");
            }
            return true;
        } catch (RuntimeException e) {
            log.error("ticket校验失败", e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(String id, String ticket, String publicKey) {
        try {
            ticketMap.put(id, ticket);
            return new RSA(null, publicKey).encryptBase64(ticket, KeyType.PublicKey);
        } catch (RuntimeException ex) {
            log.error("加密Ticket失败", ex);
            ticketMap.remove(id);
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
