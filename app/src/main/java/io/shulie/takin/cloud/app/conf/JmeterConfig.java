package io.shulie.takin.cloud.app.conf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.lang.reflect.Field;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import io.shulie.takin.cloud.app.service.jmeter.SaveService;
import io.shulie.takin.cloud.constant.JmeterPluginsConstant;

/**
 * JMeter配置
 *
 * @author chenhongqiao@shulie.com
 */
@Slf4j
@Configuration
public class JmeterConfig {
    private static final String JMETER_UTILS_CLASS = "org.apache.jmeter.util.JMeterUtils";

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
                boolean mkdirsResult = extDir.mkdirs();
                log.info("JmeterConfig#afterLoaded,mkdirs.result={}", mkdirsResult);
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
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ClassNotFoundException | NoSuchFieldException | IOException e) {
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
                JmeterPluginsConstant.getFiles().put(resource.getFilename(), file);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
