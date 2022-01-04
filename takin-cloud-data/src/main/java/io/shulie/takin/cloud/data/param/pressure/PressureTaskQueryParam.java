package io.shulie.takin.cloud.data.param.pressure;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: liyuanba
 * @Date: 2021/12/28 4:01 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PressureTaskQueryParam extends Page<PressureTaskEntity> {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 租户主键
     */
    private Long tenantId;
    /**
     * 管理者id，或者操作者id
     */
    private String envCode;
    /**
     * 场景ID
     */
    private Long sceneId;
    /**
     * 场景类型
     */
    private PressureSceneEnum sceneType;
    /**
     * 状态：0压测引擎启动中，1压测中，2压测停止，3失败
     */
    private Integer status;
    /**
     * 查询多个状态：0压测引擎启动中，1压测中，2压测停止，3失败
     */
    private List<Integer> statuses;
    /**
     * 管理者id，或者操作者id
     */
    private Long adminId;
}
