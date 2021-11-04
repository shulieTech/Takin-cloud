package io.shulie.takin.cloud.data.result.scenemanage;

import java.util.Date;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * @author hezhongqi
 * @date 2021/8/3 16:11
 */
@Data
public class SceneManageResult {

    private Long id;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 参考数据字典 场景状态
     */
    private Integer status;

    /**
     * 压测场景类型：0普通场景，1流量调试
     */
    private Integer type;

    /**
     * 最新压测时间
     */
    private Date lastPtTime;

    /**
     * 施压配置
     */
    private String ptConfig;

    /**
     * 脚本类型：0-Jmeter 1-Gatling
     */
    private Integer scriptType;

    /**
     * 是否删除：0-否 1-是
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 扩展字段
     */
    private String features;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 最后修改人
     */
    private String updateName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 脚本解析结果
     */
    private String scriptAnalysisResult;
}
