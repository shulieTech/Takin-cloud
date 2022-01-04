package io.shulie.takin.cloud.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 中间件包表(MiddlewareJar)实体类
 *
 * @author liuchuan
 * @date 2021-06-01 10:40:50
 */
@Data
@TableName(value = "t_middleware_jar")
public class MiddlewareJarEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;

    @TableLogic
    private Integer isDeleted;

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
