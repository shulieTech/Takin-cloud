package io.shulie.takin.cloud.model.callback;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.shulie.takin.cloud.model.callback.Sla.SlaInfo;
import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * SLA触发
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Sla extends Base<List<SlaInfo>> {
    private CallbackType type = CallbackType.SLA;

    @Data
    @Accessors(chain = true)
    public static class SlaInfo {
        /**
         * 关键词
         */
        private String ref;
        /**
         * 附加数据
         * <p>原样返回</p>
         */
        private String attach;
        /**
         * 施压任务主键
         */
        private long pressureId;
        /**
         * 施压任务实例主键
         */
        private long pressureExampleId;
        /**
         * 资源主键
         */
        private long resourceId;
        /**
         * 资源实例主键
         */
        private long resourceExampleId;
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
