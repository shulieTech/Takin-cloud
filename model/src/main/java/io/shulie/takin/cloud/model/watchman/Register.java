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

    @Data
    @Accessors(chain = true)
    public static class Body {
        /**
         * 关键词
         */
        private String ref;
        /**
         * 创建时间
         * <p>默认为对象创建时间</p>
         */
        private Long timeOfCreate;
        /**
         * 到期时间
         * <p>默认为世界末日</p>
         */
        private Long timeOfValidity;
    }
}
