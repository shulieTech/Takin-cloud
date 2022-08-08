package io.shulie.takin.cloud.data.entity;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 脚本任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_script")
@Accessors(chain = true)
public class ScriptEntity {
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
     * 回调地址
     */
    private String callbackUrl;
    /**
     * 任务内容
     */
    private String content;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 是否完成
     */
    private Boolean completed;
    /**
     * 任务结果
     */
    private String message;
    /**
     * 结束时间
     */
    private Date endTime;
}
