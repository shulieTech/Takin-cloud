package io.shulie.takin.cloud.app.service.impl;

import org.springframework.stereotype.Service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.KeyType;

import io.shulie.takin.cloud.app.service.TicketService;

/**
 * Ticket服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class TicketServiceImpl implements TicketService {

    /**
     * {@inheritDoc}
     */
    @Override
    public String generate() {
        return RandomUtil.randomString(128);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(String ticket, String publicKey) {
        return new RSA(null, publicKey).encryptBase64(ticket, KeyType.PublicKey);
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
