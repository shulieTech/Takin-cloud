package io.shulie.takin.cloud.open.req.statistics;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.web.app.response.statistics
 * @date 2020/11/30 9:23 下午
 */
@Data
public class PressureTotalReq extends CloudUserCommonRequestExt implements Serializable {
    private String type;
    private String startTime;
    private String endTime;
    /**
     * 压测脚本
     */
    private List<Long> scriptIds;
}
