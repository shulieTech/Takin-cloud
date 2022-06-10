package io.shulie.takin.cloud.app.service.jmeter;

import lombok.Data;
import org.apache.jorphan.collections.HashTree;

/**
 * ClassName:    ScriptWrapper
 * Package:    io.shulie.takin.cloud.app.service.jmeter
 * Description:
 * Datetime:    2022/6/6   11:29
 * Author:   chenhongqiao@shulie.com
 */
@Data
public class ScriptWrapper {
    // Used by ScriptWrapperConverter
    public String version = "";

    public HashTree testPlan;
}
