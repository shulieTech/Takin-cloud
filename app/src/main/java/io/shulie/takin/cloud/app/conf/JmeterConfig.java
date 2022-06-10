package io.shulie.takin.cloud.app.conf;

import cn.hutool.core.io.FileUtil;
import io.shulie.takin.cloud.app.classloader.JmeterLibClassLoader;
import io.shulie.takin.cloud.app.service.jmeter.SaveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
    }

    @Bean
    public JmeterLibClassLoader jmeterLibClassLoader() {
        JmeterLibClassLoader loader = JmeterLibClassLoader.getInstance();
        afterLoaded();
        return loader;
    }

    private void afterLoaded(){
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

    public void loadJmeterClass(JmeterLibClassLoader loader) {
        try {
            //获取lib
            List<File> libFiles = getJmeterLibFiles();
            URL[] urls = new URL[libFiles.size()];
            //加载Jmeter Class
            for (File libFile : libFiles) {
                URL url = new URL("file:" + libFile.getAbsolutePath());
                loader.addURL(url);
            }
//            Thread.currentThread().setContextClassLoader(loader);
            for (File libFile : libFiles) {
                JarFile jarFile = new JarFile(libFile);
                Enumeration<JarEntry> es = jarFile.entries();
                while (es.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) es.nextElement();
                    String name = jarEntry.getName();

                    if (name != null && name.endsWith(".class") && StringUtils.indexOf(name, "$") == -1) {//只解析了.class文件，没有解析里面的jar包
                        //默认去系统已经定义的路径查找对象，针对外部jar包不能用
                        try {
                            Class clazz = loader.loadClass(name.replace("/", ".").substring(0, name.length() - 6));//自己定义的loader路径可以找到
                            if (StringUtils.indexOf(name, "SampleSaveConfiguration") != -1) {
//                                Class.forName(clazz.getName());
//                                Class.forName(clazz.getName()).getDeclaredConstructor().newInstance();
                            }
                            log.info("clazz:{}", clazz.getName());
                        } catch (Error e) {
//                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<File> getJmeterLibFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources("classpath:jmeter/lib/");
        List<File> files = new ArrayList<>();
        for (Resource resource : resources) {
            File file = resource.getFile();
            if (file.isDirectory()) {
                List<File> childFiles = getChildJarFiles(file);
                files.addAll(childFiles);
            } else if (Objects.equals(FileUtil.getSuffix(file), "jar")) {
                files.add(file);
            }
        }
        return files;
    }

    private List<File> getChildJarFiles(File dirFile) {
        List<File> files = new ArrayList<>();
        for (File file : dirFile.listFiles()) {
            if (file.isDirectory()) {
                List<File> childFiles = getChildJarFiles(file);
                files.addAll(childFiles);
            } else if (Objects.equals(FileUtil.getSuffix(file), "jar")) {
                files.add(file);
            }
        }
        return files;
    }

}
