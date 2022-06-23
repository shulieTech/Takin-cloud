package io.shulie.takin.cloud.app;

import cn.hutool.core.collection.CollectionUtil;
import io.shulie.takin.cloud.app.classloader.AppParentClassLoader;
import io.shulie.takin.cloud.app.classloader.JmeterLibClassLoader;
import io.shulie.takin.cloud.app.service.jmeter.SaveService;
import io.shulie.takin.cloud.constant.JmeterPluginsConstant;
import org.apache.jmeter.extractor.json.jsonpath.JSONManager;
import org.apache.jmeter.modifiers.BeanShellPreProcessor;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.collections.HashTree;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ClassName:    ScriptTests
 * Package:    io.shulie.takin.cloud.app
 * Description:
 * Datetime:    2022/6/6   14:14
 * Author:   chenhongqiao@shulie.com
 */
@SpringBootTest
public class ScriptTests {
    private static final Logger log = LoggerFactory.getLogger(ScriptTests.class);

    @Test
    public void loadJmeterProperties() {
        ArrayList<File> files = new ArrayList<>();
        for (Map.Entry<String, File> fileEntry : JmeterPluginsConstant.localPluginFiles.entrySet()) {
            files.add(fileEntry.getValue());
        }
        files.add(new File("/Users/phine/data/plugins/jmeter-plugins-height.jar"));
        files.add(new File("/Users/phine/data/plugins/score-plugin.jar"));
        files.add(new File("/Users/phine/data/plugins/plugin-common-1.0-SNAPSHOT.jar"));
        files.add(new File("/Users/phine/data/nfs_dir/scriptfile/111/chksum.jar"));
//        files.add(new File("/Users/phine/data/plugins/ApacheJMeter_core.jar"));

        JmeterLibClassLoader libClassLoader = JmeterLibClassLoader.getInstance();
        libClassLoader.loadJars(files);
        AppParentClassLoader instance = AppParentClassLoader.getInstance();
        instance.loadJars(files);


//        String jmxFile ="/usr/local/apache-jmeter-5.4.1/jmx/kafka2.5.1.jmx";
        String jmxFile = "/Users/phine/data/nfs_dir/scriptfile/111/生成二维码.jmx";
//        String jmxFile ="/usr/local/apache-jmeter-5.4.1/jmx/PID2.jmx";
        try {
            HashTree hashTree = SaveService.loadTree(new File(jmxFile));
            List<BeanShellPreProcessor> shells = new ArrayList<>();
            getHashTreeValue(hashTree, BeanShellPreProcessor.class, shells);
            Class<?> aClass = Class.forName("org.apache.jmeter.util.BeanShellInterpreter", false, this.getClass().getClassLoader());
            Object o = aClass.getDeclaredConstructor().newInstance();

            Method set = aClass.getDeclaredMethod("set", String.class, Object.class);
            set.setAccessible(true);
            set.invoke(o, "log", log);
            set.invoke(o, "vars", new JMeterVariables());
            //bshInterpreter.set("vars", vars);

            Method eval = aClass.getDeclaredMethod("eval", String.class);
//            set("log", logger);//$NON-NLS-1$
            //bshInterpreter.set("vars", vars);

            eval.setAccessible(true);
//            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
//            Thread.currentThread().setContextClassLoader(jmeterLibClassLoader);

            Object invoke = eval.invoke(o, shells.get(0).getProperty("script").toString());
//            eval.invoke(o, "String str=\"1111\"");
//            Thread.currentThread().setContextClassLoader(oldClassLoader);
            System.out.println(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private <T> void getHashTreeValue(HashTree hashTree, Class<T> t, List<T> objs) {
        for (Object o : hashTree.keySet()) {
            if (Objects.equals(o.getClass().getName(), t.getName())) {
                objs.add((T) o);
            }
            if (CollectionUtil.isNotEmpty(hashTree.get(o))) {
                getHashTreeValue(hashTree.get(o), t, objs);
            }
        }
        return;
    }
}
