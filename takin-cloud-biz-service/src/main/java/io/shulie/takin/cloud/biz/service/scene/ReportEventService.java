package io.shulie.takin.cloud.biz.service.scene;

import java.util.Map;

/**
 * @ClassName ReportEventService
 * @Description
 * @Author qianshui
 * @Date 2020/7/20 下午3:40
 */
public interface ReportEventService {

    Map<String, String> queryAndCalcRtDistribute(String tableName, String bindRef);

}
