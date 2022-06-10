package io.shulie.takin.cloud.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import io.shulie.takin.cloud.app.classloader.JmeterLibClassLoader;
import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.app.service.jmeter.SaveService;
import io.shulie.takin.cloud.app.util.JmeterScriptUtil;
import io.shulie.takin.cloud.model.request.ScriptBuildRequest;
import io.shulie.takin.cloud.model.request.ScriptCheckRequest;
import io.shulie.takin.cloud.model.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.modifiers.BeanShellPreProcessor;
import org.apache.jorphan.collections.HashTree;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * ClassName:    ScriptServiceImpl
 * Package:    io.shulie.takin.cloud.app.service.impl
 * Description: 脚本服务实现
 * Datetime:    2022/5/19   11:32
 * Author:   chenhongqiao@shulie.com
 */
@Service
@Slf4j
public class ScriptServiceImpl implements ScriptService {

    @Value("${watchman.nfs-path}")
    private String nfsPath;

    @Resource
    private JmeterLibClassLoader jmeterLibClassLoader;

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
        String path = nfsPath + scriptCheckRequest.getScriptPath();
        //检测压测脚本是否存在
        File jmxFile = new File(path);
        if (!jmxFile.exists()) {
            return ApiResult.fail(String.format("脚本不存在，请检测脚本路径：%s", path));
        }
        //检测是否有插件
        if (StringUtils.isNotBlank(scriptCheckRequest.getPluginPaths())) {
            //加载插件
            String[] plugins = scriptCheckRequest.getPluginPaths().split(",");
            List<File> pluginFiles = new ArrayList<>();
            for (String plugin : plugins) {
                if (StringUtils.startsWith(plugin, "/")) {
                    return ApiResult.fail("插件路径应该为相对路径");
                }
                File pluginFile = new File(nfsPath + plugin);
                if (!pluginFile.exists()) {
                    return ApiResult.fail(String.format("插件不存在，请检测插件路径：%s", path));
                }
                pluginFiles.add(pluginFile);
            }
            jmeterLibClassLoader.loadJars(pluginFiles);

        }
        //读取脚本内容&校验基础脚本
        HashTree hashTree = SaveService.loadTree(jmxFile);
        //校验BeanShell
        boolean shellFlag = chekBeanShell(hashTree);
        if (!shellFlag) {
            return ApiResult.fail("BeanShell校验失败，请检查相关依赖插件是否上传");
        }

        //校验JavaSampler

        //校验CsvDataSet
        List<String> csvConfigs = new ArrayList<>();
        if(StringUtils.isNotBlank(scriptCheckRequest.getScriptPath())){
            csvConfigs = Arrays.asList(scriptCheckRequest.getCsvPaths().split(","));
        }
        boolean csvFlag = chekCsvDataSet(hashTree, csvConfigs);
        if (!shellFlag) {
            return ApiResult.fail("csv校验失败，请检查相关csv文件是否上传");
        }
        //卸载插件
        return null;
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
            Object o = aClass.getDeclaredConstructor().newInstance();
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
                eval.invoke(o, script);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean chekCsvDataSet(HashTree hashTree, List<String> csvConfigs) {
        try {
            //提取beanShell
            List<CSVDataSet> csvDataSets = new ArrayList<>();
            getHashTreeValue(hashTree, CSVDataSet.class, csvDataSets);
            if (CollectionUtil.isEmpty(csvDataSets)) {
                return true;
            }

            Class<?> aClass = Class.forName("org.apache.jmeter.util.BeanShellInterpreter", false, this.getClass().getClassLoader());
            Object o = aClass.getDeclaredConstructor().newInstance();
            Method eval = aClass.getDeclaredMethod("eval", String.class);
            eval.setAccessible(true);
            for (CSVDataSet csvDataSet : csvDataSets) {
                boolean flag = csvDataSet.getProperty("TestElement.enabled").getBooleanValue();
                if (!flag) {
                    continue;
                }
                //提取filename
                String csvFileName = csvDataSet.getProperty("filename").getStringValue();
                if(StringUtils.isBlank(csvFileName)){
                    continue;
                }
                //校验csvFile
                if(csvConfigs.isEmpty()){
                    return false;
                }
                String csvConfig = nameMatch(csvConfigs, csvFileName);
                if(StringUtils.isBlank(csvConfig)){
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
        String rawFileName = null;
        if (rawFilePath.contains("/")) {
            rawFileName = rawFilePath.substring(rawFilePath.lastIndexOf("/") + 1);
        }
        if (rawFilePath.contains("\\")) {
            rawFileName = rawFilePath.substring(rawFilePath.lastIndexOf("\\") + 1);
        }
        for (String csvConfig : csvConfigs) {
            String csvFileName = null;
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
                objs.add((T) o);
            }
            if (CollectionUtil.isNotEmpty(hashTree.get(o))) {
                getHashTreeValue(hashTree.get(o), t, objs);
            }
        }
        return;
    }

    /**
     * 卸载插件
     */
    private void unInstallPlugin() {

    }
}
