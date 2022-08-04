package io.shulie.takin.cloud.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import io.shulie.takin.cloud.app.classloader.AppParentClassLoader;
import io.shulie.takin.cloud.app.classloader.JmeterLibClassLoader;
import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.app.service.jmeter.SaveService;
import io.shulie.takin.cloud.app.util.JmeterScriptUtil;
import io.shulie.takin.cloud.constant.JmeterPluginsConstant;
import io.shulie.takin.cloud.model.request.ScriptBuildRequest;
import io.shulie.takin.cloud.model.request.ScriptCheckRequest;
import io.shulie.takin.cloud.model.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.engine.PreCompiler;
import org.apache.jmeter.engine.TurnElementsOn;
import org.apache.jmeter.modifiers.BeanShellPreProcessor;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.java.sampler.JavaSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 脚本服务实现
 *
 * @author chenhongqiao@shulie.com
 * @date 2022/5/19   11:32
 */
@Service
@Slf4j
public class ScriptServiceImpl implements ScriptService {

    @Value("${watchman.nfs-path}")
    private String nfsPath;

    private static final Object lockObj = new Object();

    @Override
    public String buildJmeterScript(ScriptBuildRequest scriptRequest) {
        Document document = DocumentHelper.createDocument();
        //创建基础脚本
        Element rootHashEle = JmeterScriptUtil.buildBase(document);
        //创建测试计划
        Element testPlanHashEle = JmeterScriptUtil.buildTestPlan(rootHashEle, scriptRequest.getName());
        //创建线程组
        Element threadGroupHashEle = JmeterScriptUtil.buildThreadGroup(testPlanHashEle);
        //创建Http信息头
        JmeterScriptUtil.buildHttpHeader(threadGroupHashEle, scriptRequest.getHeaders());
        //创建csv
        JmeterScriptUtil.buildCsvData(threadGroupHashEle, scriptRequest.getDatas());

        URL url = null;
        try {
            url = new URL(scriptRequest.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //创建取样器
        JmeterScriptUtil.buildHttpSampler(threadGroupHashEle, url, scriptRequest.getMethod(), scriptRequest.getBody());

        return JmeterScriptUtil.getDocumentStr(document);
    }

    @Override
    public String buildPressureJmeterScript() {
        return null;
    }

    @Override
    public ApiResult<Object> checkJmeterScript(ScriptCheckRequest scriptCheckRequest) {
        if (StringUtils.startsWith(scriptCheckRequest.getScriptPath(), "/")) {
            return ApiResult.fail("脚本路径应该为相对路径");
        }
        String path = StringUtils.trimToEmpty(new StringBuilder().append(nfsPath).append("/").append(scriptCheckRequest.getScriptPath()).toString());
        //检测压测脚本是否存在
        File jmxFile = new File(path);
        if (!jmxFile.exists()) {
            return ApiResult.fail(String.format("脚本不存在，请检测脚本路径：%s", path));
        }
        //检测是否有插件
        List<File> pluginFiles = new ArrayList<>();
        if (StringUtils.isNotBlank(scriptCheckRequest.getPluginPaths())) {
            //加载插件
            String[] plugins = scriptCheckRequest.getPluginPaths().split(",");
            for (String plugin : plugins) {
                File pluginFile;
                if (StringUtils.startsWith(plugin, "/")) {
                    String name = plugin.substring(plugin.lastIndexOf("/") + 1);
                    pluginFile = JmeterPluginsConstant.getFiles().getOrDefault(name, null);
                    if (Objects.isNull(pluginFile)) {
                        continue;
                    }
                } else {
                    String pluginPath = StringUtils.trim(new StringBuilder().append(nfsPath).append("/").append(plugin).toString());
                    pluginFile = new File(pluginPath);
                    if (!pluginFile.exists()) {
                        return ApiResult.fail(String.format("插件不存在，请检测插件路径：%s", pluginPath));
                    }
                }
                pluginFiles.add(pluginFile);
            }
        }
        HashTree hashTree;
        synchronized (lockObj) {
            try {

                //加载插件
                installPlugin(pluginFiles);
                //读取脚本内容&校验基础脚本
                hashTree = SaveService.loadTree(jmxFile);
                //初始化前置编译器
                HashTree test = hashTree;
                PreCompiler compiler = new PreCompiler();
                test.traverse(compiler);
                test.traverse(new TurnElementsOn());
                //校验BeanShell
                boolean shellFlag = chekBeanShell(hashTree);
                if (!shellFlag) {
                    return ApiResult.fail("BeanShell校验失败，请检查相关依赖的插件是否上传");
                }

                //校验JavaSampler
                boolean javaFlag = chekJavaSampler(hashTree);
                if (!javaFlag) {
                    return ApiResult.fail("JavaSampler校验失败，请检查配置项[classname]依赖的插件是否上传");
                }
            } finally {
                //卸载插件
                unInstallPlugin();
            }
        }

        //校验CsvDataSet
        List<String> csvConfigs = new ArrayList<>();
        if (StringUtils.isNotBlank(scriptCheckRequest.getCsvPaths())) {
            String[] temps = scriptCheckRequest.getCsvPaths().split(",");
            for (String csvPath : temps) {
                if (StringUtils.startsWith(csvPath, "/")) {
                    return ApiResult.fail("CSV文件路径应该为相对路径");
                }
                csvPath = StringUtils.trimToEmpty(new StringBuilder().append(nfsPath).append("/").append(csvPath).toString());
                File csvFile = new File(csvPath);
                if (!csvFile.exists()) {
                    return ApiResult.fail(String.format("CSV文件不存在，请检测CSV文件路径：%s", csvPath));
                }
                csvConfigs.add(csvPath);
            }
        }
        boolean csvFlag = chekCsvDataSet(hashTree, csvConfigs);
        if (!csvFlag) {
            return ApiResult.fail("csv校验失败，请检查相关csv文件是否上传");
        }
        return ApiResult.success("脚本验证成功");
    }

    private boolean chekBeanShell(HashTree hashTree) {
        try {
            //提取beanShell
            List<BeanShellPreProcessor> shells = new ArrayList<>();
            getHashTreeValue(hashTree, BeanShellPreProcessor.class, shells);
            if (CollectionUtil.isEmpty(shells)) {
                return true;
            }

            Class<?> aClass = Class.forName("org.apache.jmeter.util.BeanShellInterpreter", false, this.getClass().getClassLoader());
            Object o = aClass.newInstance();
            //环境设置
            Method set = aClass.getDeclaredMethod("set", String.class, Object.class);
            set.setAccessible(true);
            set.invoke(o, "log", log);
            set.invoke(o, "vars", new JMeterVariables());
            set.invoke(o, "ctx", JMeterContextService.getContext());
            set.invoke(o, "props", JMeterUtils.getJMeterProperties());
            set.invoke(o, "threadName", Thread.currentThread().getName());
            set.invoke(o, "sampler", new HTTPSamplerBase() {
                @Override
                protected HTTPSampleResult sample(java.net.URL url, String s, boolean b, int i) {
                    return null;
                }
            });
            set.invoke(o, "sampleResult", new SampleResult());

            Method eval = aClass.getDeclaredMethod("eval", String.class);
            eval.setAccessible(true);
            for (BeanShellPreProcessor shell : shells) {
                boolean flag = shell.getProperty("TestElement.enabled").getBooleanValue();
                if (!flag) {
                    continue;
                }
                //提取beanShell中的script
                String script = shell.getProperty("script").getStringValue();
                //校验script
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(AppParentClassLoader.getInstance());
                eval.invoke(o, script);
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }

            //清除class
            //bsh.BshClassManager

            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private boolean chekJavaSampler(HashTree hashTree) {
        try {
            //提取beanShell
            List<JavaSampler> javas = new ArrayList<>();
            getHashTreeValue(hashTree, JavaSampler.class, javas);
            if (CollectionUtil.isEmpty(javas)) {
                return true;
            }
            for (JavaSampler javaSampler : javas) {
                boolean flag = javaSampler.getProperty("TestElement.enabled").getBooleanValue();
                if (!flag) {
                    continue;
                }
                //提取class
                String clazz = javaSampler.getProperty("classname").toString();
                //校验
                Class.forName(clazz, false, JmeterLibClassLoader.getInstance());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean chekCsvDataSet(HashTree hashTree, List<String> csvConfigs) {
        try {
            //提取beanShell
            List<CSVDataSet> csvDataSets = new ArrayList<>();
            getHashTreeValue(hashTree, CSVDataSet.class, csvDataSets);
            if (CollectionUtil.isEmpty(csvDataSets)) {
                return true;
            }
            for (CSVDataSet csvDataSet : csvDataSets) {
                boolean flag = csvDataSet.getProperty("TestElement.enabled").getBooleanValue();
                if (!flag) {
                    continue;
                }
                //提取filename
                String csvFileName = csvDataSet.getProperty("filename").getStringValue();
                if (StringUtils.isBlank(csvFileName)) {
                    continue;
                }
                //校验csvFile
                if (csvConfigs.isEmpty()) {
                    return false;
                }
                String csvConfig = nameMatch(csvConfigs, csvFileName);
                if (StringUtils.isBlank(csvConfig)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String nameMatch(List<String> csvConfigs, String rawFilePath) {
        String rawFileName = rawFilePath;
        if (rawFilePath.contains("/")) {
            rawFileName = rawFilePath.substring(rawFilePath.lastIndexOf("/") + 1);
        }
        if (rawFilePath.contains("\\")) {
            rawFileName = rawFilePath.substring(rawFilePath.lastIndexOf("\\") + 1);
        }
        for (String csvConfig : csvConfigs) {
            String csvFileName = csvConfig;
            if (csvConfig.contains("/")) {
                csvFileName = csvConfig.substring(csvConfig.lastIndexOf("/") + 1);
            }
            if (csvConfig.contains("\\")) {
                csvFileName = csvConfig.substring(csvConfig.lastIndexOf("\\") + 1);
            }
            if (StringUtils.equalsIgnoreCase(csvFileName, rawFileName)) {
                return csvConfig;
            }
        }
        return null;
    }

    private <T> void getHashTreeValue(HashTree hashTree, Class<T> t, List<T> objs) {
        for (Object o : hashTree.keySet()) {
            if (Objects.equals(o.getClass().getName(), t.getName())) {
                objs.add((T)o);
            }
            if (CollectionUtil.isNotEmpty(hashTree.get(o))) {
                getHashTreeValue(hashTree.get(o), t, objs);
            }
        }
        return;
    }

    private void installPlugin(List<File> pluginFiles) {
        if (CollectionUtils.isEmpty(pluginFiles)) {return;}
        JmeterLibClassLoader libClassLoader = JmeterLibClassLoader.getInstance();
        libClassLoader.loadJars(pluginFiles);
        AppParentClassLoader instance = AppParentClassLoader.getInstance();
        instance.loadJars(pluginFiles);
    }

    /**
     * 卸载插件
     */
    private void unInstallPlugin() {
        JmeterLibClassLoader.getInstance().reset();
        AppParentClassLoader.getInstance().reset();
    }
}
