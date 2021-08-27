package io.shulie.takin.cloud.biz.input.statistics;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.web.app.response.statistics
 * @date 2020/11/30 9:23 下午
 */
@Data
public class PressureTotalInput {
    private String type;
    private String startTime;
    private String endTime;
    /**
     * 脚本ids
     */
    private List<Long> scriptIds;
}
