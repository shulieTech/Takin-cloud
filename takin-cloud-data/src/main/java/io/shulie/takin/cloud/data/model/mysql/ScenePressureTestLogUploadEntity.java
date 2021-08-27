package io.shulie.takin.cloud.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 压测上传jmeter日志任务状态表
 */
@Data
@TableName(value = "t_scene_jmeterlog_upload")
public class ScenePressureTestLogUploadEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 报告ID
     */
    @TableField(value = "report_id")
    private Long reportId;

    /**
     * 用户ID
     */
    @TableField(value = "customer_id")
    private Long customerId;

    /**
     * 压测任务状态：1-启动中；2-启动成功；3-压测失败；4-压测完成
     */
    @TableField(value = "task_status")
    private Integer taskStatus;

    /**
     * 日志上传状态：0-未上传；1-上传中；2-上传完成
     */
    @TableField(value = "upload_status")
    private Integer uploadStatus;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "modify_time")
    private Date modifyTime;

    /**
     * 文件上传行数
     */
    @TableField(value = "upload_count")
    private Long uploadCount;

    /**
     * 文件名称
     */
    @TableField(value = "file_name")
    private String fileName;
}