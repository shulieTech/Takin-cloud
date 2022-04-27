package io.shulie.takin.cloud.app.entity;

import java.util.Date;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 回调日志
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_callback_log")
public class CallbackLogEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 回调主键
     */
    private Long callbackId;
    /**
     * 请求路径
     */
    private String requestUrl;
    /**
     * 请求数据
     */
    private byte[] requestData;
    /**
     * 请求时间
     */
    private Date requestTime;
    /**
     * 响应数据
     */
    private byte[] responseData;
    /**
     * 响应时间
     */
    private Date responseTime;
    /**
     * 是否完成
     */
    private Boolean complete;
}
