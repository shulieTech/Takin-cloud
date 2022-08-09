package io.shulie.takin.cloud.app.service.impl;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.io.IOException;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Service;
import cn.hutool.core.exceptions.ValidateException;

import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.data.entity.ScriptEntity;
import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.service.ScriptMapperService;
import io.shulie.takin.cloud.model.callback.script.ResultReport;
import io.shulie.takin.cloud.model.request.job.script.BuildRequest;

/**
 * 脚本服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class ScriptServiceImpl implements ScriptService {

    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CommandService commandService;
    @javax.annotation.Resource
    CallbackService callbackService;
    @javax.annotation.Resource(name = "scriptMapperServiceImpl")
    ScriptMapperService scriptMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long announce(Long watchmanId, String callbackUrl, String attach,
        String scriptPath, List<String> dataPath,
        List<String> attachmentPath, List<String> pluginPath) {
        // 1. 组装任务数据
        List<String> emptyList = new ArrayList<>(0);
        Map<String, Object> content = new HashMap<>(4);
        content.put("script", scriptPath);
        content.put("data", dataPath == null ? emptyList : dataPath);
        content.put("plugin", pluginPath == null ? emptyList : pluginPath);
        content.put("attachment", attachmentPath == null ? emptyList : attachmentPath);
        // 2. 保存数据
        ScriptEntity scriptEntity = new ScriptEntity()
            .setAttach(attach)
            .setWatchmanId(watchmanId)
            .setCallbackUrl(callbackUrl)
            .setContent(jsonService.writeValueAsString(content));
        scriptMapper.save(scriptEntity);
        // 3. 写入命令
        Long scriptId = scriptEntity.getId();
        commandService.announceScript(scriptEntity.getId());
        return scriptId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(Long id, Boolean completed, String message) {
        // 0.1 数据长度处理
        message = CharSequenceUtil.subWithLength(message, 0, 255);
        // 0.2 获取数据实例
        ScriptEntity scriptEntity = scriptMapper.getById(id);
        if (Objects.nonNull(scriptEntity)) {
            // 1. 更新状态
            boolean updateResult = scriptMapper.lambdaUpdate()
                .set(ScriptEntity::getCompleted, completed)
                .set(ScriptEntity::getEndTime, new Date())
                .set(ScriptEntity::getMessage, message)
                .isNull(ScriptEntity::getCompleted)
                .update();
            // 2. 回调控制台
            if (Boolean.TRUE.equals(updateResult)) {
                ResultReport reportData = new ResultReport().setResult(completed)
                    .setAttach(scriptEntity.getAttach())
                    .setMessage(message);
                callbackService.create(scriptEntity.getCallbackUrl(), CallbackType.FILE_RESOURCE_PROGRESS,
                    jsonService.writeValueAsString(reportData));
            }
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptEntity entity(Long id) {
        return scriptMapper.getById(id);
    }
}
