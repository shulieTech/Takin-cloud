package io.shulie.takin.cloud.app.model.watchman;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 * 注册
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public class Register {
    private Header header;
    private Header body;

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

    public static void main(String[] args) throws JsonProcessingException {
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
        System.out.println("header" + headerString);
        System.out.println("body" + bodyString);
        String base64HeaderString = Base64.encodeUrlSafe(headerString);
        String base64BodyString = Base64.encodeUrlSafe(bodyString);
        String secret = "shulie@2022";
        System.out.println("head " + base64HeaderString);
        System.out.println("body " + base64BodyString);
        System.out.println("secret " + secret);
        HMac hMac = SecureUtil.hmacSha256(secret);
        String verifySignature = hMac.digestBase64(StrUtil.format("{}.{}", base64HeaderString, base64BodyString), true);
        String ref = StrUtil.format("{}.{}.{}", base64HeaderString, base64BodyString, verifySignature);
        String refSign = SecureUtil.md5(ref);
        System.out.println(ref);
        System.out.println(refSign);
    }
}
