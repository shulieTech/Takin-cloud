package io.shulie.takin.cloud.app.classloader;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ClassName:    JmeterLibClassLoader
 * Package:    io.shulie.takin.cloud.app.classloader
 * Description:
 * Datetime:    2022/6/7   18:15
 * Author:   chenhongqiao@shulie.com
 */
@Slf4j
public class JmeterLibClassLoader extends URLClassLoader {
    private Map<String, Class<?>> loadedClasses;
    private static JmeterLibClassLoader INSTANCE;
    private static ClassLoader webappClassLoader;

    private JmeterLibClassLoader() {
        super(new URL[0], JmeterLibClassLoader.class.getClassLoader());
        this.loadedClasses = new HashMap<>();
    }

    public static JmeterLibClassLoader getInstance() {
        if (INSTANCE == null) { // 一重检查
            synchronized (JmeterLibClassLoader.class) {
                if (INSTANCE == null) { // 二重检查
                    INSTANCE = new JmeterLibClassLoader();
                    try {
                        webappClassLoader = JmeterLibClassLoader.class
                                .getClassLoader();
                    } catch (Exception e) {
                        log.error("设置classloader到容器中时出现错误！");
                    }
                }
            }
        }
        return INSTANCE;
    }

    public void loadJars(List<File> jars) {
        if (CollectionUtil.isEmpty(jars)) {
            return;
        }
        try {
            //加载Jmeter Class
            for (File jar : jars) {
                URL url = new URL("jar:file:" + jar.getAbsolutePath() + "!/");
                this.addURL(url);
            }
            for (File jar : jars) {
                JarFile jarFile = new JarFile(jar);
                Enumeration<JarEntry> es = jarFile.entries();
                while (es.hasMoreElements()) {
                    JarEntry jarEntry = es.nextElement();
                    String name = jarEntry.getName();

                    if (name.endsWith(".class") && name.indexOf("$") == -1) {//只解析了.class文件，没有解析里面的jar包
                        //默认去系统已经定义的路径查找对象，针对外部jar包不能用
                        try {
                            this.loadClass(name.replace("/", ".").substring(0, name.length() - 6));//自己定义的loader路径可以找到
                        } catch (Error e) {
//                            log.error(e.getMessage());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Error | IOException e) {
            e.printStackTrace();
        }
    }

    public void addURL(URL url) {
        log.debug("Add '{}'", url);
        super.addURL(url);
    }

    @SuppressWarnings({"rawtypes"})
    public Class loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {

        if (loadedClasses.containsKey(name)) {
            return loadedClasses.get(name);
        }
        Class clazz = null;
        if (Objects.isNull(clazz)) {
            clazz = super.loadClass(name, resolve);
        }
        loadedClasses.put(name, clazz);
        return clazz;
    }

    public void reset() {
        loadedClasses.clear();
        loadedClasses = null;
        INSTANCE = null;
    }

}
