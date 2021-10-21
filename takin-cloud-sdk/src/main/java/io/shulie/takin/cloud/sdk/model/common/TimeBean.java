package io.shulie.takin.cloud.sdk.model.common;


import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/17 下午9:51
 */
@Data
public class TimeBean {

    private Long time;

    private String unit;

    public TimeBean() {

    }

    public TimeBean(Long time, String unit) {
        this.time = time;
        this.unit = unit;
    }
}
