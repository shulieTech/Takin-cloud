package io.shulie.takin.cloud.app.classloader;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
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
    private Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();

    private static JmeterLibClassLoader INSTANCE;

    private static ClassLoader webappClassLoader;

//    private JmeterLibClassLoader() {
//        super(new URL[0], JmeterLibClassLoader.class.getClassLoader().getParent());
//    }
    private JmeterLibClassLoader() {
        super(new URL[0], JmeterLibClassLoader.class.getClassLoader());
    }

    public static JmeterLibClassLoader getInstance() {
        if (INSTANCE == null) { // 一重检查
            synchronized (JmeterLibClassLoader.class) {
                if (INSTANCE == null) { // 二重检查
                    INSTANCE = new JmeterLibClassLoader();
                    try {
                        INSTANCE.webappClassLoader = JmeterLibClassLoader.class
                                .getClassLoader();
//                        INSTANCE.addThisToParentClassLoader(INSTANCE.webappClassLoader);
                    } catch (Exception e) {
                        log.error("设置classloader到容器中时出现错误！");
                    }
                }
            }
        }
        return INSTANCE;
    }

    public void loadJars(List<File> jars) {
        if(CollectionUtil.isEmpty(jars)){
            return;
        }
        try {
            //加载Jmeter Class
            for (File jar : jars) {
                URL url = new URL("file:" + jar.getAbsolutePath());
                this.addURL(url);
            }
//            Thread.currentThread().setContextClassLoader(loader);
            for (File jar : jars) {
                JarFile jarFile = new JarFile(jar);
                Enumeration<JarEntry> es = jarFile.entries();
                while (es.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) es.nextElement();
                    String name = jarEntry.getName();

                    if (name != null && name.endsWith(".class") && name.indexOf("$") == -1) {//只解析了.class文件，没有解析里面的jar包
                        //默认去系统已经定义的路径查找对象，针对外部jar包不能用
                        try {
                            Class clazz = this.loadClass(name.replace("/", ".").substring(0, name.length() - 6));//自己定义的loader路径可以找到
                        } catch (Error e) {
                            log.error(e.getMessage());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Error e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addURL(URL url) {
        log.debug("Add '{}'", url);
        super.addURL(url);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (loadedClasses.containsKey(name)) {
            return loadedClasses.get(name);
        }
//        Class clazzBase = null;
//        try {
//            clazzBase = Class.forName(name);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        if(Objects.nonNull(clazzBase)){
//            loadedClasses.put(name, clazzBase);
//            return clazzBase;
//        }
        Class clazz = super.loadClass(name, resolve);
        loadedClasses.put(name, clazz);
        return clazz;
    }


    private void addThisToParentClassLoader(ClassLoader classLoader) throws Exception {
        Field field = ClassLoader.class.getDeclaredField("parent");
        field.setAccessible(true);
        field.set(classLoader, this);
    }
}
