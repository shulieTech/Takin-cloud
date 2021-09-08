package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/5/13 下午9:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineTaskLogQueryParm extends PagingDevice implements Serializable {
    private static final long serialVersionUID = -4979635191936291226L;
    private Long taskId;
}
