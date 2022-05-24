package io.shulie.takin.cloud.model.watchman;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.Accessors;

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
}
