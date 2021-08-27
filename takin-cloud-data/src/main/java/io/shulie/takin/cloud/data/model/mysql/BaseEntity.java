package io.shulie.takin.cloud.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/1 10:49 上午
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 220459039796531099L;

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

}
