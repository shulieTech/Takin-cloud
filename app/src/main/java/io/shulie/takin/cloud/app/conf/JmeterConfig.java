package io.shulie.takin.cloud.app.conf;

import io.shulie.takin.cloud.app.classloader.JmeterLibClassLoader;
import io.shulie.takin.cloud.app.service.jmeter.SaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * ClassName:    JmeterConfig
 * Package:    io.shulie.takin.cloud.app.conf
 * Description:
 * Datetime:    2022/6/6   15:13
 * Author:   chenhongqiao@shulie.com
 */
@Configuration
@Slf4j
public class JmeterConfig {
    private final static String JMETER_UTILS_CLASS = "org.apache.jmeter.util.JMeterUtils";

    @PostConstruct
    public void init() {
        SaveService.initProps();
        afterLoaded();
    }

    @Bean
    public JmeterLibClassLoader jmeterLibClassLoader() {
        JmeterLibClassLoader loader = JmeterLibClassLoader.getInstance();
        return loader;
    }

    private void afterLoaded() {
        try {
            Class<?> clazz = Class.forName(JMETER_UTILS_CLASS);
            Field appProperties = clazz.getDeclaredField("appProperties");
            appProperties.setAccessible(true);
            appProperties.set(null, new Properties());
            Class.forName("org.apache.jmeter.samplers.SampleSaveConfiguration");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
