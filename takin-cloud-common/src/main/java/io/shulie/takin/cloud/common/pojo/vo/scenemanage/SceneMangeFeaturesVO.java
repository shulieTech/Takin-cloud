package io.shulie.takin.cloud.common.pojo.vo.scenemanage;

import lombok.Data;

/**
 * 场景扩展字段, json 转的类
 *
 * @author liuchuan
 * @date 2021/4/26 11:00 上午
 */
@Data
public class SceneMangeFeaturesVO {

    /**
     * 关联的脚本id
     * 对应的是 web 下的 脚本实例id
     */
    private Long scriptId;

    /**
     * 配置类型
     */
    private Integer configType;

    /**
     * 时间间隔
     */
    private Integer scheduleInterval;

}
