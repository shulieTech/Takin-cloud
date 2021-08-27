package io.shulie.takin.cloud.biz.collector.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: io.shulie.takin.cloud.collector.bean
 * @Date 2020-05-11 14:30
 */
@Getter
@Setter
public class LoadInfo {

    private int cpuNum;
    private String load_1;
    private String load_2;
    private String load_3;
}
