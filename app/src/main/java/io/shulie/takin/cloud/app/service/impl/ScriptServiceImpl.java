package io.shulie.takin.cloud.app.service.impl;

import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.app.util.JmeterScriptUtil;
import io.shulie.takin.cloud.model.request.ScriptBuildRequest;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

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
    @Override
    public String buildJmeterScript(ScriptBuildRequest scriptRequest) {
        Document document = DocumentHelper.createDocument();
//        Element root = document.addElement("");
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
}
