package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2020/5/13 下午9:23
 * @Description:
 */
@Data
public class MachineTaskLogQueryParm extends PagingDevice implements Serializable {
    private static final long serialVersionUID = -4979635191936291226L;
    private Long taskId;
}
