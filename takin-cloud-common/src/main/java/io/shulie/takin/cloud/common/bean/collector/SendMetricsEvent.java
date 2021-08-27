package io.shulie.takin.cloud.common.bean.collector;

import lombok.Data;

/**
 * 事件发送对象
 *
 * @Author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: io.shulie.takin.entity
 * @Date 2020-04-20 15:20
 */
@Data
public class SendMetricsEvent extends Metrics {

    private Long sceneId;
    private Long reportId;
    private Long customerId;

}
