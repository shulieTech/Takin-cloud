package io.shulie.takin.cloud.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.shulie.takin.cloud.common.pojo.AbstractEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuchuan
 * @date 2021/6/1 10:49 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity extends AbstractEntry {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value="gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value="gmt_modified")
    private Date gmtModified;
}
