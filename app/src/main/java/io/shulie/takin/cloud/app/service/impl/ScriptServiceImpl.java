package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import cn.hutool.core.exceptions.ValidateException;

import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.model.request.job.script.BuildRequest;

/**
 * 脚本服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class ScriptServiceImpl implements ScriptService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Long announce(String scriptPath, List<String> dataFilePath, List<String> attachmentsPath) {
        return 0L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(Long id, Boolean completed, String message) {
        // TODO 回调控制台
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build(BuildRequest scriptRequest) {
        org.dom4j.Document document = org.dom4j.DocumentHelper.createDocument();
        //创建基础脚本
        org.dom4j.Element rootHashEle = JmeterScriptUtil.buildBase(document);
        //创建测试计划
        org.dom4j.Element testPlan = JmeterScriptUtil.buildTestPlan(rootHashEle, scriptRequest.getName());
        //创建线程组
        org.dom4j.Element threadGroupHashEle = JmeterScriptUtil.buildThreadGroup(testPlan);
        //创建Http信息头
        org.dom4j.Element httpHeader = JmeterScriptUtil.buildHttpHeader(threadGroupHashEle, scriptRequest.getHeaders());
        log.debug("请求头:{}", httpHeader);
        //创建csv
        org.dom4j.Element csv = JmeterScriptUtil.buildCsvData(threadGroupHashEle, scriptRequest.getDatas());
        log.debug("csv:{}", csv);
        //创建取样器
        try {
            java.net.URL url = new java.net.URL(scriptRequest.getUrl());
            org.dom4j.Element sampler = JmeterScriptUtil.buildHttpSampler(threadGroupHashEle, url, scriptRequest.getMethod(), scriptRequest.getBody());
            log.debug("sampler:{}", sampler);
            return JmeterScriptUtil.getDocumentStr(document);
        } catch (RuntimeException | IOException e) {
            log.error("构建脚本出错.", e);
            throw new ValidateException("构建脚本出错", e);
        }
    }
}
