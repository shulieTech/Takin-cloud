package io.shulie.takin.cloud.sdk.model.request.machine;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

/**
 * ClassName:    MachineAddParam
 * Package:    com.pamirs.takin.entity.domain.vo.machine
 * Description:
 * Datetime:    2022/7/9   14:02
 * Author:   chenhongqiao@shulie.com
 */
@Data
public class MachineUpdateReq extends ContextExt {
    private String nodeName;
    private String updateName;
}
