package io.shulie.takin.cloud.sdk.model.common;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/6/24 10:37 上午
 */
@Data
public class StopReasonBean {
    private String type;
    private String description;

    public StopReasonBean() {
    }

    public StopReasonBean(String type, String description) {
        this.type = type;
        this.description = description;
    }

}
