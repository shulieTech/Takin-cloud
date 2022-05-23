package io.shulie.takin.cloud.common.bean.scenemanage;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qianshui
 * @date 2020/4/18 上午11:13
 */
@Data
@Accessors(chain = true)
public class SceneManageQueryOpitons {

    /**
     * 业务活动
     */
    private Boolean includeBusinessActivity = false;

    /**
     * 脚本文件
     */
    private Boolean includeScript = false;

    /**
     * SLA配置
     */
    private Boolean includeSLA = false;
}
