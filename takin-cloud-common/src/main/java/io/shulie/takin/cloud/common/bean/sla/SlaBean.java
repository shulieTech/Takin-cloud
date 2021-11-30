package io.shulie.takin.cloud.common.bean.sla;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/18 5:07 下午
 */
@Data
public class SlaBean {
    private String ruleName;
    private String businessActivity;
    private String bindRef;
    private String rule;
}
