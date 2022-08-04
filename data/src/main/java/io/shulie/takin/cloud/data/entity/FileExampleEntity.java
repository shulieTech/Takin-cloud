package io.shulie.takin.cloud.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 文件实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
public class FileExampleEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 文件主键
     */
    private Long fileId;
    /**
     * 调度主键
     */
    private Long watchmanId;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件摘要(MD5)
     */
    private String sign;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 完成的大小
     */
    private Long completeSize;
    /**
     * 总总数
     */
    private Long totalSize;
    /**
     * 是否已完成
     */
    private Boolean completed;
    /**
     * 信息
     */
    private String message;

}
