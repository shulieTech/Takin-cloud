package io.shulie.takin.cloud.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 调度器
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_watchman")
public class WatchmanEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 关键词
     */
    private String ref;
    /**
     * 关键词签名
     */
    private String refSign;
    /**
     * 数据加密的公钥
     */
    private String publicKey;
}
