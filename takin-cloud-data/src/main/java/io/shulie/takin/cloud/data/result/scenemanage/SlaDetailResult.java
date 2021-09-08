package io.shulie.takin.cloud.data.result.scenemanage;

import java.io.Serializable;

import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/18 下午11:44
 */
@Data
public class SlaDetailResult implements Serializable {

    private static final long serialVersionUID = 9171434959213456889L;

    private String ruleName;

    private String businessActivity;

    private String rule;

    private EnumResult status;
}
