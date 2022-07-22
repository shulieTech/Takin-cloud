package io.shulie.takin.cloud.app.conf;

import io.shulie.takin.cloud.app.service.jmeter.SaveService;
import io.shulie.takin.cloud.constant.JmeterPluginsConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        findLocalPlugins();
    }

    private void afterLoaded() {
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources("classpath:jmeter/lib/ext/*.jar");
            File extDir = new File("lib/ext");
            if (!extDir.exists()) {
                extDir.mkdirs();
            }
            String extPath = extDir.getAbsolutePath();
            // 通过流将文件复制到file中
            for (Resource resource : resources) {
                resource.getFilename();
                File file = new File(extPath + "/ext-" + resource.getFilename());
                FileUtils.copyToFile(resource.getInputStream(), file);
            }
            resources = resourcePatternResolver.getResources("classpath:jmeter/jmeter.properties");
            Properties p = new Properties();
            InputStream is = resources[0].getInputStream();
            p.load(is);
            p.setProperty("search_paths", extPath);

            Class<?> clazz = Class.forName(JMETER_UTILS_CLASS);
            Field appProperties = clazz.getDeclaredField("appProperties");
            appProperties.setAccessible(true);
            appProperties.set(null, p);
            Class.forName("org.apache.jmeter.samplers.SampleSaveConfiguration");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * find local plugins jar
     */
    private void findLocalPlugins() {
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources("classpath:jmeter/lib/plugins/*.jar");
            // 通过流将文件复制到file中
            for (Resource resource : resources) {
                resource.getFilename();
                File file = new File("tmp-" + resource.getFilename());
                FileUtils.copyToFile(resource.getInputStream(), file);
                JmeterPluginsConstant.localPluginFiles.put(resource.getFilename(), file);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
