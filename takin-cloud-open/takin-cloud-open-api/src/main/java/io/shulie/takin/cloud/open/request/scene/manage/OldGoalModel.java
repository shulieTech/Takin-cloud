package io.shulie.takin.cloud.open.request.scene.manage;

import java.math.BigDecimal;

import io.shulie.takin.cloud.open.request.scene.manage.SceneRequest.Goal;
import lombok.Data;

/**
 * 旧的压测目标模型
 *
 * @author 张天赐
 */
@Data
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public class OldGoalModel {
    /**
     * 成功率
     */
    private BigDecimal successRate;
    /**
     * 响应时间
     */
    private Integer RT;
    /**
     * 达标率
     */
    private BigDecimal SA;
    /**
     * TPS
     */
    private Integer TPS;

    /**
     * 从{@link Goal}转换
     *
     * @param goal 新的目标实体
     * @return 旧的目标实体
     */
    public static OldGoalModel from(SceneRequest.Goal goal) {
        return new OldGoalModel() {{
            setSA(BigDecimal.valueOf(goal.getSa()));
            setRT(goal.getRt());
            setSuccessRate(BigDecimal.valueOf(goal.getSr()));
            setTPS(goal.getTps());
        }};
    }

    /**
     * 转换为{@link Goal}
     *
     * @return 新的目标实体
     */
    public SceneRequest.Goal to() {
        return new SceneRequest.Goal() {{
            setRt(getRT());
            setTps(getTPS());
            setSa(getSA().doubleValue());
            setSr(getSuccessRate().doubleValue());
        }};
    }
}
