package io.shulie.plugin.engine;

import io.shulie.takin.plugin.framework.extension.spring.annotation.PropertySource;
import io.shulie.takin.plugin.framework.extension.springboot.SpringBootPlugin;
import org.pf4j.PluginWrapper;

/**
 * @author zhaoyong
 */
@PropertySource("engine.properties")
public class EnginePlugin extends SpringBootPlugin {
    public EnginePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }
}
