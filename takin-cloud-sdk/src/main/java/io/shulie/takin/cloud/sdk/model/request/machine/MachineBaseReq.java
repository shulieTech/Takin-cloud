package io.shulie.takin.cloud.sdk.model.request.machine;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

/**
 * ClassName:    MachineBaseReq
 * Package:    io.shulie.takin.cloud.sdk.model.request.machine
 * Description:
 * Datetime:    2022/7/9   14:28
 * Author:   chenhongqiao@shulie.com
 */
@Data
public class MachineBaseReq extends ContextExt {
    private String nodeName;
}