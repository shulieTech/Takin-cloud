package io.shulie.takin.cloud.app.classloader;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ClassName:    AppParentClassLoader
 * Package:    io.shulie.takin.cloud.app.classloader
 * Description:
 * Datetime:    2022/6/10   14:45
 * Author:   chenhongqiao@shulie.com
 */
@Slf4j
public class AppParentClassLoader extends URLClassLoader {

    private static AppParentClassLoader INSTANCE;
    private static ClassLoader webappClassLoader = AppParentClassLoader.class.getClassLoader();
    private static ClassLoader webParentClassLoader = AppParentClassLoader.class.getClassLoader().getParent();

    public AppParentClassLoader() {
        super(new URL[0], AppParentClassLoader.class.getClassLoader().getParent());
    }

    public static AppParentClassLoader getInstance() {
        if (INSTANCE == null) { // 一重检查
            synchronized (JmeterLibClassLoader.class) {
                if (INSTANCE == null) { // 二重检查
                    INSTANCE = new AppParentClassLoader();
                    try {
                        INSTANCE.addThisToParentClassLoader(INSTANCE.webappClassLoader);
                    } catch (Exception e) {
                        log.error("设置classloader到容器中时出现错误！");
                    }
                }
            }
        }
        return INSTANCE;
    }

    public void loadJars(List<File> jars) {
        try {
            //加载Jmeter Class
            for (File jar : jars) {
                URL url = new URL("file:" + jar.getAbsolutePath());
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

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {
            try {
                clazz = this.getParent().loadClass(name);
            } catch (ClassNotFoundException e) {
                // Don't want to see this.
            }
            if (clazz == null) {
                clazz = super.loadClass(name, resolve);
            }
        }
        return clazz;
    }

    public void reset() {
        if (Objects.nonNull(INSTANCE)) {
            try {
                super.close();
                Field field = ClassLoader.class.getDeclaredField("parent");
                field.setAccessible(true);
                field.set(webappClassLoader, webParentClassLoader);
            } catch (Exception e) {
                log.error("设置classloader到容器中时出现错误！");
            }
        }
        INSTANCE = null;
    }

    private void addThisToParentClassLoader(ClassLoader classLoader) throws Exception {
        Field field = ClassLoader.class.getDeclaredField("parent");
        field.setAccessible(true);
        field.set(classLoader, this);
    }
}
