package com.pamirs.takin.entity.domain.vo.machine;

import lombok.Data;

/**
 * ClassName:    MachineAddParam
 * Package:    com.pamirs.takin.entity.domain.vo.machine
 * Description:
 * Datetime:    2022/7/9   14:02
 * Author:   chenhongqiao@shulie.com
 */
@Data
public class MachineAddParam {
    private String nodeIp;
    private String name;
    private String password;
}
