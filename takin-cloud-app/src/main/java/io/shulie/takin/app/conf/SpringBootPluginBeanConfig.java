package io.shulie.takin.app.conf;

import java.util.*;

import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.plugin.framework.core.configuration.IConfiguration;
import io.shulie.takin.plugin.framework.core.configuration.impl.DefaultConfiguration;
import io.shulie.takin.plugin.framework.extension.spring.AutoPluginManager;
import io.shulie.takin.plugin.framework.extension.spring.SpringBaseExtension;
import io.shulie.takin.plugin.framework.extension.spring.SpringBasicBeanExtension;
import io.shulie.takin.plugin.framework.extension.spring.SpringInitExtension;
import io.shulie.takin.plugin.framework.extension.spring.SpringLogbackExtension;
import io.shulie.takin.plugin.framework.extension.spring.SpringMybatisPlusExtension;
import io.shulie.takin.plugin.framework.extension.spring.SwaggerExtension;
import io.shulie.takin.plugin.framework.extension.springboot.SpringAutoConfigExtension;
import io.shulie.takin.plugin.framework.extension.springboot.SpringBootInitExtension;
import io.shulie.takin.plugin.framework.extension.springmvc.SpringMvcExtension;
import lombok.Data;
import org.pf4j.RuntimeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author xiaobin.zfb|xiaobin@shulie.io
 * @since 2021/7/22 7:29 下午
 */
@Data
@Component
@ConfigurationProperties(prefix = "takin.plugin")
public class SpringBootPluginBeanConfig {
    /**
     * running mode
     * dev env: development、dev
     * <p>
     * prod env: deployment、prod
     */
    private String runMode = "prod";

    /**
     * plugin path
     */
    public String pluginPath = "./plugins";

    /**
     * plugin config file path
     */
    private String pluginConfigFilePath = "pluginConfigs";

    /**
     * plugin config file path
     */
    private String pluginPrefixPath = "/api/plugin";

    /**
     * plugin config file path
     */
    private boolean enablePluginPrefixPathPluginId = true;

    /**
     * enable plugins
     */
    private Set<String> enablePluginIds = new HashSet<>();

    /**
     * disable plugins
     */
    private Set<String> disablePluginIds = new HashSet<>();

    /**
     * sort plugins
     */
    private List<String> sortPluginIds = new ArrayList<>();

    /**
     * sort plugins
     */
    private String version = "0.0.0";

    /**
     * sort plugins
     */
    private boolean exactVersionAllowed = false;

    private Map<String, String> arguments;

    @Bean
    public IConfiguration configuration() {
        return new DefaultConfiguration.Builder()
                .withEnvironment(RuntimeMode.byName(runMode))
                .withPluginPath(pluginPath)
                .withPluginConfigFilePath(pluginConfigFilePath)
                .withPluginPrefixPath(pluginPrefixPath)
                .withArguments(arguments)
                .withEnablePluginIds(enablePluginIds)
                .withDisablePluginIds(disablePluginIds)
                .withSortPluginIds(sortPluginIds)
                .withExactVersionAllowed(exactVersionAllowed)
                .withEnablePluginPrefixPathPluginId(enablePluginPrefixPathPluginId)
                .withVersion(version)
                .build();
    }

    @Bean
    public PluginManager pluginManager(@Autowired IConfiguration configuration) {
        PluginManager pluginManager = new AutoPluginManager(configuration);
        pluginManager.addApplicationExtension(new SpringInitExtension());
        pluginManager.addApplicationExtension(new SpringBootInitExtension());
        pluginManager.addApplicationExtension(new SpringLogbackExtension());
        pluginManager.addApplicationExtension(new SpringBaseExtension());
        pluginManager.addApplicationExtension(new SpringBasicBeanExtension());
        pluginManager.addApplicationExtension(new SpringAutoConfigExtension());
        pluginManager.addApplicationExtension(new SpringMybatisPlusExtension());
        pluginManager.addApplicationExtension(new SpringMvcExtension());
        pluginManager.addApplicationExtension(new SwaggerExtension());
        return pluginManager;
    }
}