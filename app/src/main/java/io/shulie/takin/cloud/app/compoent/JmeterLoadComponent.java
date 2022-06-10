//package io.shulie.takin.cloud.app.compoent;
//
//import cn.hutool.core.io.FileUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.util.*;
//import java.util.jar.JarEntry;
//import java.util.jar.JarFile;
//
///**
// * ClassName:    JmeterLoadCompoent
// * Package:    io.shulie.takin.cloud.app.compoent
// * Description:
// * Datetime:    2022/6/7   17:11
// * Author:   chenhongqiao@shulie.com
// */
//@Component
//public class JmeterLoadComponent {
//    private static ApplicationContext applicationContext;
//
//    @SuppressWarnings("static-access")
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//
//    public static ApplicationContext getApplicationContext() {
//        return applicationContext;
//    }
//
//    public static DefaultListableBeanFactory getBeanFactory() {
//        return (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
//    }
//
//    @PostConstruct
//    public void init(){
//        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();//所有的Class对象
//        try {
//            //获取lib
//
//            List<File> libFiles = getJmeterLibFiles();
//
//            URL[] urls = new URL[libFiles.size()];
//            //加载Jmeter Class
//            int i = 0;
//            for (File libFile : libFiles) {
//                URL url = new URL("file:" + libFile.getAbsolutePath());
//                urls[i] = url;
//                i++;
//            }
//
//            URLClassLoader loader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());//自己定义的classLoader类，把外部路径也加到load路径里，使系统去该路经load对象
//            getBeanFactory().setBeanClassLoader(loader);
//            Thread.currentThread().setContextClassLoader(loader);
//            for (File libFile : libFiles) {
//                JarFile jarFile = new JarFile(libFile);
//                Enumeration<JarEntry> es = jarFile.entries();
//                System.out.println("libFile:" + jarFile.getName());
//                while (es.hasMoreElements()) {
//                    JarEntry jarEntry = (JarEntry) es.nextElement();
//                    String name = jarEntry.getName();
//                    if (name != null && name.endsWith(".class") && StringUtils.indexOf(name, "$") == -1) {//只解析了.class文件，没有解析里面的jar包
//                        //默认去系统已经定义的路径查找对象，针对外部jar包不能用
//                        //Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(name.replace("/", ".").substring(0,name.length() - 6));
//                        try {
//                            Class<?> c = loader.loadClass(name.replace("/", ".").substring(0, name.length() - 6));//自己定义的loader路径可以找到
//                            System.out.println(c);
//                            classes.add(c);
//                        } catch (Error e) {
////                            e.printStackTrace();
//                        } catch (ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        if (StringUtils.indexOf(name, "TestRig") != -1) {
//                            System.out.println(1);
//                        }
//                    }
//                }
//            }
//            String className = "com.helger.commons.functional.IThrowingConsumer";
//            Class clazz = Class.forName(className);
//            Method[] declaredMethods = clazz.getDeclaredMethods();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private List<File> getJmeterLibFiles() throws IOException {
//        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
//        Resource[] resources = resourcePatternResolver.getResources("classpath:jmeter/lib/");
//        List<File> files = new ArrayList<>();
//        for (Resource resource : resources) {
//            File file = resource.getFile();
//            if (file.isDirectory()) {
//                List<File> childFiles = getChildJarFiles(file);
//                files.addAll(childFiles);
//            } else if (Objects.equals(FileUtil.getSuffix(file), "jar")) {
//                files.add(file);
//            }
//        }
//        return files;
//    }
//
//    private List<File> getChildJarFiles(File dirFile) {
//        List<File> files = new ArrayList<>();
//        for (File file : dirFile.listFiles()) {
//            if (file.isDirectory()) {
//                List<File> childFiles = getChildJarFiles(file);
//                files.addAll(childFiles);
//            } else if (Objects.equals(FileUtil.getSuffix(file), "jar")) {
//                files.add(file);
//            }
//        }
//        return files;
//    }
//
//
//
//}
