package io.shulie.takin.cloud.app.service;

/**
 * ticket服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface TicketService {

    /**
     * 生成签名
     *
     * @return 随机ticket
     */
    String generate();

    /**
     * 加密ticket
     *
     * @param ticket    需要加密的ticket
     * @param publicKey 公钥
     * @return 加密后的ticket
     */
    String encrypt(String ticket, String publicKey);

    /**
     * 放篡改签名
     *
     * @param bytes     防篡改的内容
     * @param timestamp 时间戳
     * @param ticket    需要加密的ticket
     * @return 签名
     */
    String sign(byte[] bytes, long timestamp, String ticket);
}
