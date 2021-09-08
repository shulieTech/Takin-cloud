package io.shulie.takin.cloud.biz.service.scene;

import java.util.Map;

/**
 * @author qianshui
 * @date 2020/7/20 下午3:40
 */
public interface ReportEventService {

    Map<String, String> queryAndCalcRtDistribute(String tableName, String bindRef);

}
