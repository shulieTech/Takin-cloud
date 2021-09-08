package io.shulie.takin.cloud.biz.service.record;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.schedule.ScheduleRecordDTO;
import com.pamirs.takin.entity.domain.vo.schedule.ScheduleRecordQueryVO;

/**
 * @author qianshui
 * @date 2020/5/9 下午2:10
 */
public interface ScheduleRecordService {

    PageInfo<ScheduleRecordDTO> queryPageList(ScheduleRecordQueryVO queryVO);
}
