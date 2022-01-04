package io.shulie.takin.cloud.data.model.mysql;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: liyuanba
 * @Date: 2021/12/28 3:19 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDeleteEntity extends BaseEntity {
    @TableLogic
    private Integer isDeleted;
}
