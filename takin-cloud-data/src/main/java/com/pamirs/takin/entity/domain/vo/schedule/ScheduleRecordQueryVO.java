package com.pamirs.takin.entity.domain.vo.schedule;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.common.beans.page.PagingDevice;

/**
 * @author qianshui
 * @date 2020/5/9 下午2:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleRecordQueryVO extends PagingDevice implements Serializable {

    private Long sceneId;
}
