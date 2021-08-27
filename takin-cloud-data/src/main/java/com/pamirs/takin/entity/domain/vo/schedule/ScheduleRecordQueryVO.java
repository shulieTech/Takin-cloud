package com.pamirs.takin.entity.domain.vo.schedule;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @ClassName ScheduleRecordQueryVO
 * @Description
 * @Author qianshui
 * @Date 2020/5/9 下午2:02
 */
@Data
public class ScheduleRecordQueryVO extends PagingDevice implements Serializable {

    private Long sceneId;
}
