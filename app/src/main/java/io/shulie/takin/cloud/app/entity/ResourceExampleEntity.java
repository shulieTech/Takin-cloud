package io.shulie.takin.cloud.app.entity;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 资源实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_resource_example")
public class ResourceExampleEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 资源镜像
     */
    private String image;
    /**
     * 资源主键
     */
    private Long resourceId;
    /**
     * 调度器主键
     */
    private Long watchmanId;
    /**
     * 需要的CPU
     */
    private String cpu;
    /**
     * 需要的内存
     */
    private String memory;
    /**
     * 限制的CPU
     */
    private String limitCpu;
    /**
     * 限制的内存
     */
    private String limitMemory;
    /**
     * 创建时间
     */
    private Date createTime;
}
