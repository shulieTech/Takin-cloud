package io.shulie.takin.cloud.model.watchman;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.Accessors;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.core.text.CharSequenceUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 注册
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Slf4j
@Accessors(chain = true)
public class Register {
    private Header header;
    private Body body;

    private String ref;
    private String refSign;

    @Data
    public static class Header {
        /**
         * 加密算法
         * <p>verify signature</p>
         */
        private String alg;
        /**
         * 加密算法
         * <p>注册信息摘要</p>
         */
        private String sign;
    }

    @Data
    public static class Body {
        /**
         * 关键词
         */
        private String ref;
        /**
         * 创建时间
         */
        private Long timeOfCreate;
        /**
         * 到期时间
         */
        private Long timeOfValidity;
    }

    public static void main(String[] args) {
        try {
            Header header = new Header();
            header.setAlg("HS256");
            header.setSign("MD5");
            Body body = new Body();
            body.setRef("tianci");
            body.setTimeOfValidity(253402271999999L);
            body.setTimeOfCreate(System.currentTimeMillis());
            ObjectMapper objectMapper = new ObjectMapper();
            String headerString = objectMapper.writeValueAsString(header);
            String bodyString = objectMapper.writeValueAsString(body);

            String base64HeaderString = Base64.encodeUrlSafe(headerString);
            String base64BodyString = Base64.encodeUrlSafe(bodyString);
            String secret = "shulie@2022";
            log.info("head(base64) " + base64HeaderString);
            log.info("body(base64)" + base64BodyString);
            log.info("secret " + secret);
            HMac hMac = SecureUtil.hmacSha256(secret);
            String verifySignature = hMac.digestBase64(CharSequenceUtil.format("{}.{}", base64HeaderString, base64BodyString), true);
            String ref = CharSequenceUtil.format("{}.{}.{}", base64HeaderString, base64BodyString, verifySignature);
            String refSign = SecureUtil.md5(ref);

            Register register = new Register()
                .setRefSign(refSign)
                .setHeader(header)
                .setBody(body)
                .setRef(ref);
            log.info("{}", register);
        } catch (Exception e) {
            log.error("发生错误.\n", e);
        }
    }
}
