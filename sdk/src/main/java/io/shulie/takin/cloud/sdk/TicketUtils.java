package io.shulie.takin.cloud.sdk;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * ticket工具类
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@SuppressWarnings("unused")
public class TicketUtils {

    private TicketUtils() {}

    /**
     * 解密
     *
     * @param data      明文
     * @param publicKey 公钥
     * @return 密文
     */
    public static String encrypt(String data, String publicKey) {
        // 如果公钥为空则不加密
        if (CharSequenceUtil.isBlank(publicKey)) {return data;}
        return SecureUtil.rsa(null, publicKey)
            .encryptBase64(data, KeyType.PublicKey);
    }

    /**
     * 解密
     *
     * @param data       密文
     * @param privateKey 私钥
     * @return 明文
     */
    public static String decrypt(String data, String privateKey) {
        // 如果私钥为空则不解密
        if (CharSequenceUtil.isBlank(privateKey)) {return data;}
        return SecureUtil.rsa(privateKey, null)
            .decryptStr(data, KeyType.PrivateKey);
    }

    /**
     * 签名
     *
     * @param timestamp 时间戳
     * @param ticket    -
     * @return 签名
     */
    public static String sign(long timestamp, String ticket) {
        return sign(null, timestamp, ticket);
    }

    /**
     * 签名
     *
     * @param bytes     防篡改的内容
     * @param timestamp 时间戳
     * @param ticket    -
     * @return 签名
     */
    public static String sign(byte[] bytes, long timestamp, String ticket) {
        return SecureUtil.md5(CharSequenceUtil.join("",
            StrUtil.utf8Str(bytes),
            StrUtil.utf8Str(timestamp),
            StrUtil.utf8Str(ticket)));
    }
}
