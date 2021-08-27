package io.shulie.takin.cloud.data.result.statistics;

import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.web.app.response.statistics
 * @date 2020/11/30 9:21 下午
 */
@Data
public class ReportTotalResult {
    private Integer count;
    private Integer success;
    private Integer fail;
}
