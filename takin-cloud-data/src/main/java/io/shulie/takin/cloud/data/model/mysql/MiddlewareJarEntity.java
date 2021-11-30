package io.shulie.takin.cloud.data.model.mysql;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 中间件包表(MiddlewareJar)实体类
 *
 * @author liuchuan
 * @date 2021-06-01 10:40:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_middleware_jar")
public class MiddlewareJarEntity extends BaseEntity {

    /**
     * 中间件中文名称
     */
    private String name;

    /**
     * 中间件类型
     */
    private String type;

    /**
     * 支持的包状态, 1 已支持, 2 待支持, 3 无需支持
     */
    private Integer status;

    /**
     * 中间件名称
     */
    private String artifactId;

    /**
     * 中间件组织名称
     */
    private String groupId;

    /**
     * 中间件版本
     */
    private String version;

    /**
     * artifactId_groupId_version
     */
    private String agv;

}
