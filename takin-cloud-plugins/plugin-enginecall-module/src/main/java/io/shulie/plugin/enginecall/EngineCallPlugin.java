package io.shulie.plugin.enginecall;

import io.shulie.takin.plugin.framework.extension.spring.annotation.PropertySource;
import io.shulie.takin.plugin.framework.extension.springboot.SpringBootPlugin;
import org.pf4j.PluginWrapper;

/**
 * @author zhaoyong
 */
@PropertySource("engine-call.properties")
public class EngineCallPlugin extends SpringBootPlugin {

    public EngineCallPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }
}
