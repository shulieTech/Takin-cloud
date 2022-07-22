package io.shulie.takin.cloud.app;

import cn.hutool.core.collection.CollectionUtil;
import io.shulie.takin.cloud.app.classloader.AppParentClassLoader;
import io.shulie.takin.cloud.app.classloader.JmeterLibClassLoader;
import io.shulie.takin.cloud.app.service.jmeter.SaveService;
import io.shulie.takin.cloud.constant.JmeterPluginsConstant;
import org.apache.jmeter.engine.PreCompiler;
import org.apache.jmeter.engine.TurnElementsOn;
import org.apache.jmeter.extractor.json.jsonpath.JSONManager;
import org.apache.jmeter.modifiers.BeanShellPreProcessor;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.apache.jorphan.util.Converter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

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
    private final static String JMETER_UTILS_CLASS = "org.apache.jmeter.util.JMeterUtils";
    @Test
    public void loadJmeterProperties() {
        ArrayList<File> files = new ArrayList<>();
        for (Map.Entry<String, File> fileEntry : JmeterPluginsConstant.localPluginFiles.entrySet()) {
            files.add(fileEntry.getValue());
        }
//        files.add(new File("/Users/phine/data/plugins/jmeter-plugins-height.jar"));
//        files.add(new File("/Users/phine/data/plugins/score-plugin.jar"));
//        files.add(new File("/Users/phine/data/plugins/plugin-common-1.0-SNAPSHOT.jar"));
        files.add(new File("/Users/phine/data/nfs_dir/scriptfile/111/chksum.jar"));
//        files.add(new File("/Users/phine/data/plugins/ApacheJMeter_core.jar"));
        files.add(new File("/Users/phine/Downloads/jackson-core-asl-1.9.11.jar"));
        files.add(new File("/Users/phine/Downloads/jackson-mapper-asl-1.9.11.jar"));

        JmeterLibClassLoader libClassLoader = JmeterLibClassLoader.getInstance();
        libClassLoader.loadJars(files);
        AppParentClassLoader instance = AppParentClassLoader.getInstance();
        instance.loadJars(files);


//        String jmxFile ="/usr/local/apache-jmeter-5.4.1/jmx/kafka2.5.1.jmx";
//        String jmxFile = "/Users/phine/data/nfs_dir/scriptfile/111/生成二维码.jmx";
//        String jmxFile ="/usr/local/apache-jmeter-5.4.1/jmx/PID2.jmx";
        String jmxFile ="/Users/phine/Downloads/fota2.0下行.jmx";
//        String jmxFile ="/Users/phine/Downloads/e门店-无清单团单-发版.jmx";
        try {
//            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
//            Thread.currentThread().setContextClassLoader(AppParentClassLoader.getInstance());

            HashTree hashTree = SaveService.loadTree(new File(jmxFile));
            //todo
//            JMeterUtils.loadJMeterProperties("/Users/phine/Downloads/pressure-engine\\ 2/engines/jmeter/bin/jmeter.properties");

            HashTree test = hashTree;
            PreCompiler compiler = new PreCompiler();
            test.traverse(compiler);
            test.traverse(new TurnElementsOn());

            List<BeanShellPreProcessor> shells = new ArrayList<>();
            getHashTreeValue(hashTree, BeanShellPreProcessor.class, shells);
            Class<?> aClass = Class.forName("org.apache.jmeter.util.BeanShellInterpreter", false, this.getClass().getClassLoader());
            Object o = aClass.getDeclaredConstructor().newInstance();



            Method set = aClass.getDeclaredMethod("set", String.class, Object.class);
            set.setAccessible(true);
            set.invoke(o, "log", log);
            set.invoke(o, "vars", new JMeterVariables());
            set.invoke(o, "ctx", JMeterContextService.getContext());
            set.invoke(o,"props", JMeterUtils.getJMeterProperties());
            set.invoke(o,"threadName", Thread.currentThread().getName());
            HTTPSamplerBase base = new HTTPSamplerBase() {
                @Override
                protected HTTPSampleResult sample(java.net.URL url, String s, boolean b, int i) {
                    return null;
                }
            };
            set.invoke(o, "Sampler", base);
            set.invoke(o, "SampleResult", new SampleResult());



            Method eval = aClass.getDeclaredMethod("eval", String.class);
//            set("log", logger);//$NON-NLS-1$
            //bshInterpreter.set("vars", vars);

            eval.setAccessible(true);
//            String str ="int i = ${__Random(0,30,)};\n" +
//                    "log.info(i);";
//            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
//            Thread.currentThread().setContextClassLoader(jmeterLibClassLoader);
           String str = Converter.convert(shells.get(0).getProperty("script"), String.class).toString();
           log.info(str);
            Object invoke = eval.invoke(o, str);
//            eval.invoke(o, "String str=\"1111\"");
//            Thread.currentThread().setContextClassLoader(oldClassLoader);
            System.out.println(1);
//            Thread.currentThread().setContextClassLoader(contextClassLoader);
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
