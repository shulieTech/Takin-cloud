package io.shulie.takin.cloud.model.request.watchman;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import io.shulie.takin.cloud.model.watchman.Register.Body;

/**
 * 注册
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Schema(description = "调度机注册信息")
public class RegisteRequest {
    /**
     * 公钥
     */
    @Schema(description = "公钥")
    private String publicKey;
    /**
     * 主要消息体
     */
    @Schema(description = "主要消息体")
    private Body body;

    public RegisteRequest(String ref, String publicKey) {
        this(ref, 253402271999999L, publicKey);
    }

    public RegisteRequest(String ref, Long timeOfValidity, String publicKey) {
        this.setBody(new Body().setRef(ref)
            .setTimeOfCreate(System.currentTimeMillis())
            .setTimeOfValidity(timeOfValidity));
        this.setPublicKey(publicKey);
    }
}