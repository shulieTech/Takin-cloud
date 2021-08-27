package io.shulie.takin.cloud.biz.collector.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: io.shulie.takin.cloud.collector.bean
 * @Date 2020-05-11 14:31
 */
@Getter
@Setter
public class DiskUsage {

    private String diskName;
    private String util;

}
