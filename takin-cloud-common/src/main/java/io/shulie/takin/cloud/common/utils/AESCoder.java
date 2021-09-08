package io.shulie.takin.cloud.common.utils;

import java.util.Map;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;

/**
 * @author 慕白
 * @date 2019-11-11 09:28
 */
public abstract class AESCoder {

    public static final String KEY_PRADAR_PARAM = "KwBHRgqFEygN1VZC2TR7Qw==";
    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式<p>
     * 在128位--16字节下兼容C#的PKCS7Padding</p>
     */
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    /**
     * 将Cipher以密钥做键存放起来
     */
    private static final Map<String, Cipher> EN_CIPHER_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Cipher> DE_CIPHER_MAP = new ConcurrentHashMap<>();

    private static Cipher initCipher(String key, int mode) throws Exception {
        // 实例化Cipher对象，它用于完成实际的加密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        Key k = new SecretKeySpec(Base64.decodeBase64(key.getBytes()), KEY_ALGORITHM);
        // 加密模式
        cipher.init(mode, k);
        return cipher;
    }

    private static Cipher getEnCipher(String key) throws Exception {
        if (EN_CIPHER_MAP.containsKey(key)) {
            return EN_CIPHER_MAP.get(key);
        }
        Cipher cipher = initCipher(key, Cipher.ENCRYPT_MODE);
        EN_CIPHER_MAP.put(key, cipher);
        return cipher;
    }

    private static Cipher getDeCipher(String key) throws Exception {
        if (DE_CIPHER_MAP.containsKey(key)) {
            return DE_CIPHER_MAP.get(key);
        }
        Cipher cipher = initCipher(key, Cipher.DECRYPT_MODE);
        DE_CIPHER_MAP.put(key, cipher);
        return cipher;
    }

    /**
     * 加密数据
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的数据
     */
    public static String encrypt(String data, String key) {
        try {
            //执行加密操作。加密后的结果通常都会用Base64编码进行传输
            return new String(Base64.encodeBase64(getEnCipher(key)
                .doFinal(data.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENCRYPTION_AES_ERROR, "加密失败，原数据:" + data, e);
        }
    }

    /**
     * 解密数据
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return 解密后的数据
     */
    public static String decrypt(String data, String key) {
        try {
            //执行解密操作
            return new String(getDeCipher(key)
                .doFinal(Base64.decodeBase64(data.getBytes())), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENCRYPTION_AES_ERROR, "加密失败，原数据:" + data, e);
        }
    }

    public static void main(String[] args) {
        String data = "";
        String encrypt = encrypt(data, AESCoder.KEY_PRADAR_PARAM);

        System.out.println(encrypt);

        String decrypt = decrypt(encrypt, AESCoder.KEY_PRADAR_PARAM);

        System.out.println(decrypt);

    }

}
