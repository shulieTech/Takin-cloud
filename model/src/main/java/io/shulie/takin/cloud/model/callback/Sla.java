package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.model.callback.Sla.SlaInfo;
import io.shulie.takin.cloud.model.callback.basic.Basic;
import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * SLA触发
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Sla extends Basic<SlaInfo> {

    private final CallbackType type = CallbackType.SLA;

    @Data
    public static class SlaInfo {
        /**
         * 关键词
         */
        private String ref;
        /**
         * 任务实例主键
         */
        private long jobExampleId;
        /**
         * 算式目标
         * <p>(RT、TPS、SA、成功率)</p>
         */
        private Integer formulaTarget;
        /**
         * 算式符号
         * <p>(>=、>、=、<=、<)</p>
         */
        private Integer formulaSymbol;
        /**
         * 算式数值
         * <p>(用户输入)</p>
         */
        private Double formulaNumber;
        /**
         * 比较的值
         * <p>(实际变化的值)</p>
         */
        private Double number;
    }
}
