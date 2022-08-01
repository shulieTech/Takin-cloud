package io.shulie.takin.cloud.data.entity;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_resource")
public class ResourceEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 调度器主键
     */
    private Long watchmanId;
    /**
     * 资源镜像
     */
    private String image;
    /**
     * 资源总数
     */
    private Integer number;
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
    /**
     * 状态回调路径
     */
    private String callbackUrl;
}
