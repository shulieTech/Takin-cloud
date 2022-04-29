package io.shulie.takin.cloud.app.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - SLA
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_sla")
public class SlaEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 任务主键
     */
    private Long jobId;
    /**
     * 关键词
     */
    private String ref;
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
}
