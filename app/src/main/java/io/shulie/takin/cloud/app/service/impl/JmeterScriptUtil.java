package io.shulie.takin.cloud.app.service.impl;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.io.IOException;

import org.dom4j.Element;
import org.dom4j.Document;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.text.CharSequenceUtil;
import io.shulie.takin.cloud.app.util.StringWriter;
import io.shulie.takin.cloud.model.script.ScriptData;
import io.shulie.takin.cloud.model.script.ScriptHeader;

/**
 * ClassName:    JmeterScriptUtil
 * Package:    io.shulie.takin.cloud.app.util
 * Description:
 * Datetime:    2022/5/24   15:48
 *
 * @author chenhongqiao@shulie.com
 */
@Slf4j
public class JmeterScriptUtil {
    private JmeterScriptUtil() {}

    private static final String LESS_THAN = "&lt;";
    private static final String LESS_THAN_REPLACEMENT = "SHULIE_LESS_THAN_FLAG";
    private static final String GREATER_THAN = "&gt;";
    private static final String GREATER_THAN_REPLACEMENT = "SHULIE_GREATER_THAN_FLAG";
    private static final String AND = "&amp;";
    private static final String AND_REPLACEMENT = "SHULIE_AND_FLAG";
    private static final String APOS = "&apos;";
    private static final String APOS_REPLACEMENT = "SHULIE_APOS_FLAG";
    private static final String QUOTE = "&quot;";
    public static final String QUOTE_REPLACEMENT = "SHULIE_GUOTE_FLAG";

    public static final String HASH_TREE = "hashTree";
    public static final String GUI_CLASS = "guiclass";
    public static final String TEST_NAME = "testname";
    public static final String TEST_CLASS = "testclass";
    public static final String ENABLED = "enabled";
    public static final String STRING_PROP = "stringProp";
    public static final String BOOL_PROP = "boolProp";
    public static final String ELEMENT_PROP = "elementProp";
    public static final String ELEMENT_TYPE = "elementType";
    public static final String COLLECTION_PROP = "collectionProp";
    public static final String ARGUMENTS = "Arguments";
    public static final String NAME = "name";

    public static Element buildBase(Document element) {
        Element plan = element.addElement("jmeterTestPlan");
        plan.addAttribute("version", "1.2");
        plan.addAttribute("properties", "5.0");
        plan.addAttribute("jmeter", "5.4.1");
        return plan.addElement(HASH_TREE);
    }

    public static Element buildTestPlan(Element element, String testName) {
        Element plan = element.addElement("TestPlan");
        plan.addAttribute(GUI_CLASS, "TestPlanGui");
        plan.addAttribute(TEST_CLASS, "TestPlan");
        plan.addAttribute(TEST_NAME, testName);
        plan.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element stringProp = plan.addElement(STRING_PROP);
        stringProp.addAttribute(NAME, "TestPlan.comments");

        Element boolProp = plan.addElement(BOOL_PROP);
        boolProp.addAttribute(NAME, "TestPlan.functional_mode");
        boolProp.setText(Boolean.FALSE.toString());

        Element boolProp2 = plan.addElement(BOOL_PROP);
        boolProp2.addAttribute(NAME, "TestPlan.tearDown_on_shutdown");
        boolProp2.setText(Boolean.TRUE.toString());

        Element boolProp3 = plan.addElement(BOOL_PROP);
        boolProp3.addAttribute(NAME, "TestPlan.serialize_threadgroups");
        boolProp3.setText(Boolean.FALSE.toString());

        Element elementProp = plan.addElement(ELEMENT_PROP);
        elementProp.addAttribute(NAME, "TestPlan.user_defined_variables");
        elementProp.addAttribute(ELEMENT_TYPE, ARGUMENTS);
        elementProp.addAttribute(GUI_CLASS, "ArgumentsPanel");
        elementProp.addAttribute(TEST_CLASS, ARGUMENTS);
        elementProp.addAttribute(TEST_NAME, "用户定义的变量");
        elementProp.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element collectionProp = elementProp.addElement(COLLECTION_PROP);
        collectionProp.addAttribute(NAME, "Arguments.arguments");

        Element stringProp2 = plan.addElement(STRING_PROP);
        stringProp2.addAttribute(NAME, "TestPlan.user_define_classpath");

        return element.addElement(HASH_TREE);
    }

    public static Element buildThreadGroup(Element element) {
        Element threadGroup = element.addElement("ThreadGroup");
        threadGroup.addAttribute(GUI_CLASS, "ThreadGroupGui");
        threadGroup.addAttribute(TEST_CLASS, "ThreadGroup");
        threadGroup.addAttribute(TEST_NAME, "线程组");
        threadGroup.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element stringProp = threadGroup.addElement(STRING_PROP);
        stringProp.addAttribute(NAME, "ThreadGroup.on_sample_error");
        stringProp.setText("continue");

        Element elementProp = threadGroup.addElement(ELEMENT_PROP);
        elementProp.addAttribute(NAME, "ThreadGroup.main_controller");
        elementProp.addAttribute(ELEMENT_TYPE, "LoopController");
        elementProp.addAttribute(GUI_CLASS, "LoopControlPanel");
        elementProp.addAttribute(TEST_CLASS, "LoopController");
        elementProp.addAttribute(TEST_NAME, "循环控制器");
        elementProp.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element loopBoolProp = elementProp.addElement(BOOL_PROP);
        loopBoolProp.addAttribute(NAME, "LoopController.continue_forever");
        loopBoolProp.setText(Boolean.FALSE.toString());

        Element loopStringProp = elementProp.addElement(STRING_PROP);
        loopStringProp.addAttribute(NAME, "LoopController.loops");
        loopStringProp.setText("1");

        Element stringProp2 = threadGroup.addElement(STRING_PROP);
        stringProp2.addAttribute(NAME, "ThreadGroup.num_threads");
        stringProp2.setText("1");

        Element stringProp3 = threadGroup.addElement(STRING_PROP);
        stringProp3.addAttribute(NAME, "ThreadGroup.ramp_time");
        stringProp3.setText("1");

        Element boolProp = threadGroup.addElement(BOOL_PROP);
        boolProp.addAttribute(NAME, "ThreadGroup.scheduler");
        boolProp.setText(Boolean.FALSE.toString());

        Element stringProp4 = threadGroup.addElement(STRING_PROP);
        stringProp4.addAttribute(NAME, "ThreadGroup.duration");
        stringProp4.setText("");

        Element stringProp5 = threadGroup.addElement(STRING_PROP);
        stringProp5.addAttribute(NAME, "ThreadGroup.delay");
        stringProp5.setText("");

        Element boolProp2 = threadGroup.addElement(BOOL_PROP);
        boolProp2.addAttribute(NAME, "ThreadGroup.same_user_on_next_iteration");
        boolProp2.setText(Boolean.TRUE.toString());

        return element.addElement(HASH_TREE);
    }

    public static Element buildHttpHeader(Element element, List<ScriptHeader> headers) {
        Element header = element.addElement("HeaderManager");
        header.addAttribute(GUI_CLASS, "HeaderPanel");
        header.addAttribute(TEST_CLASS, "HeaderManager");
        header.addAttribute(TEST_NAME, "HTTP信息头管理器");
        header.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element collectionProp = header.addElement(COLLECTION_PROP);
        collectionProp.addAttribute(NAME, "HeaderManager.headers");
        if (!CollUtil.isEmpty(headers)) {
            for (ScriptHeader scriptHeader : headers) {
                Element elementProp = collectionProp.addElement(ELEMENT_PROP);
                elementProp.addAttribute(NAME, "");
                elementProp.addAttribute(ELEMENT_TYPE, "Header");
                Element stringPropKey = elementProp.addElement(STRING_PROP);
                stringPropKey.addAttribute(NAME, "Header.name");
                stringPropKey.setText(scriptHeader.getKey());

                Element stringPropVal = elementProp.addElement(STRING_PROP);
                stringPropVal.addAttribute(NAME, "Header.value");
                stringPropVal.setText(scriptHeader.getValue());
            }
        }
        element.addElement(HASH_TREE);
        return element;
    }

    public static Element buildCsvData(Element element, List<ScriptData> datas) {
        if (CollUtil.isEmpty(datas)) {
            return element;
        }
        for (ScriptData scriptData : datas) {
            Element data = element.addElement("CSVDataSet");
            data.addAttribute(GUI_CLASS, "TestBeanGUI");
            data.addAttribute(TEST_CLASS, "CSVDataSet");
            data.addAttribute(TEST_NAME, scriptData.getName());
            data.addAttribute(ENABLED, Boolean.TRUE.toString());

            Element stringProp = data.addElement(STRING_PROP);
            stringProp.addAttribute(NAME, "filename");
            stringProp.setText(scriptData.getPath());

            Element fileEncoding = data.addElement(STRING_PROP);
            fileEncoding.addAttribute(NAME, "fileEncoding");
            fileEncoding.setText("");

            Element variableNames = data.addElement(STRING_PROP);
            variableNames.addAttribute(NAME, "variableNames");
            variableNames.setText(scriptData.getFormat());

            Element ignoreFirstLine = data.addElement(BOOL_PROP);
            ignoreFirstLine.addAttribute(NAME, "ignoreFirstLine");
            ignoreFirstLine.setText(Objects.isNull(scriptData.getIgnoreFirstLine()) ? Boolean.FALSE.toString() : scriptData.getIgnoreFirstLine().toString());

            Element delimiter = data.addElement(STRING_PROP);
            delimiter.addAttribute(NAME, "delimiter");
            delimiter.setText(",");

            Element quotedData = data.addElement(BOOL_PROP);
            quotedData.addAttribute(NAME, "quotedData");
            quotedData.setText(Boolean.FALSE.toString());

            Element recycle = data.addElement(BOOL_PROP);
            recycle.addAttribute(NAME, "recycle");
            recycle.setText(Boolean.TRUE.toString());

            Element stopThread = data.addElement(BOOL_PROP);
            stopThread.addAttribute(NAME, "stopThread");
            stopThread.setText(Boolean.FALSE.toString());

            Element shareMode = data.addElement(STRING_PROP);
            shareMode.addAttribute(NAME, "shareMode");
            shareMode.setText("shareMode.all");

            element.addElement(HASH_TREE);
        }

        return element;
    }

    public static Element buildHttpSampler(Element element, URL queryUrl, String method, String body) {

        Element sampler = element.addElement("HTTPSamplerProxy");
        sampler.addAttribute(GUI_CLASS, "HttpTestSampleGui");
        sampler.addAttribute(TEST_CLASS, "HTTPSamplerProxy");
        sampler.addAttribute(TEST_NAME, "HTTP请求");
        sampler.addAttribute(ENABLED, Boolean.TRUE.toString());
        if (CharSequenceUtil.isNotBlank(body)) {
            Element postBodyRaw = sampler.addElement(BOOL_PROP);
            postBodyRaw.addAttribute(NAME, "HTTPSampler.postBodyRaw");
            postBodyRaw.setText(Boolean.TRUE.toString());

            Element arguments = sampler.addElement(ELEMENT_PROP);
            arguments.addAttribute(NAME, "HTTPsampler.Arguments");
            arguments.addAttribute(ELEMENT_TYPE, ARGUMENTS);

            Element collectionProp = arguments.addElement(COLLECTION_PROP);
            collectionProp.addAttribute(NAME, "Arguments.arguments");

            Element elementProp = collectionProp.addElement(ELEMENT_PROP);
            elementProp.addAttribute(NAME, "");
            elementProp.addAttribute(ELEMENT_TYPE, "HTTPArgument");

            Element boolProp = elementProp.addElement(BOOL_PROP);
            boolProp.addAttribute(NAME, "HTTPArgument.always_encode");
            boolProp.setText(Boolean.FALSE.toString());

            Element stringProp = elementProp.addElement(STRING_PROP);
            stringProp.addAttribute(NAME, "Argument.value");
            stringProp.setText(body);

            Element stringProp2 = elementProp.addElement(STRING_PROP);
            stringProp2.addAttribute(NAME, "Argument.metadata");
            stringProp2.setText("=");
        }

        Element domain = sampler.addElement(STRING_PROP);
        domain.addAttribute(NAME, "HTTPSampler.domain");
        domain.setText(queryUrl.getHost());

        Element port = sampler.addElement(STRING_PROP);
        port.addAttribute(NAME, "HTTPSampler.port");
        port.setText(queryUrl.getPort() + "");

        Element protocol = sampler.addElement(STRING_PROP);
        protocol.addAttribute(NAME, "HTTPSampler.protocol");
        protocol.setText(queryUrl.getProtocol());

        Element contentEncoding = sampler.addElement(STRING_PROP);
        contentEncoding.addAttribute(NAME, "HTTPSampler.contentEncoding");
        contentEncoding.setText("UTF-8");

        Element path = sampler.addElement(STRING_PROP);
        path.addAttribute(NAME, "HTTPSampler.path");
        StringBuilder pathQuery = new StringBuilder(queryUrl.getPath());
        if (CharSequenceUtil.isNotBlank(queryUrl.getQuery())) {
            pathQuery.append("?").append(queryUrl.getQuery());
        }
        path.setText(pathQuery.toString());

        Element methodE = sampler.addElement(STRING_PROP);
        methodE.addAttribute(NAME, "HTTPSampler.method");
        methodE.setText(method);

        Element followRedirects = sampler.addElement(BOOL_PROP);
        followRedirects.addAttribute(NAME, "HTTPSampler.follow_redirects");
        followRedirects.setText(Boolean.TRUE.toString());

        Element autoRedirects = sampler.addElement(BOOL_PROP);
        autoRedirects.addAttribute(NAME, "HTTPSampler.auto_redirects");
        autoRedirects.setText(Boolean.FALSE.toString());

        Element useKeepalive = sampler.addElement(BOOL_PROP);
        useKeepalive.addAttribute(NAME, "HTTPSampler.use_keepalive");
        useKeepalive.setText(Boolean.TRUE.toString());

        Element doMultipartPost = sampler.addElement(BOOL_PROP);
        doMultipartPost.addAttribute(NAME, "HTTPSampler.DO_MULTIPART_POST");
        doMultipartPost.setText(Boolean.FALSE.toString());

        Element embeddedUrlRe = sampler.addElement(STRING_PROP);
        embeddedUrlRe.addAttribute(NAME, "HTTPSampler.embedded_url_re");
        embeddedUrlRe.setText("");

        Element connectTimeout = sampler.addElement(STRING_PROP);
        connectTimeout.addAttribute(NAME, "HTTPSampler.connect_timeout");
        connectTimeout.setText("");

        Element responseTimeout = sampler.addElement(STRING_PROP);
        responseTimeout.addAttribute(NAME, "HTTPSampler.response_timeout");
        responseTimeout.setText("");

        element.addElement(HASH_TREE);
        return element;
    }

    public static String getDocumentStr(Document document) {
        try (StringWriter stringWriter = new StringWriter()) {
            document.write(stringWriter);
            String finalStr = stringWriter.getString();
            finalStr = specialCharRepAfter(finalStr);
            return finalStr;
        } catch (IOException e) {
            log.warn("JmeterScriptUtil#getDocumentStr", e);
            throw new IORuntimeException(e);
        }
    }

    public static String specialCharRepAfter(String content) {
        return content.replace(LESS_THAN_REPLACEMENT, LESS_THAN)
            .replace(GREATER_THAN_REPLACEMENT, GREATER_THAN)
            .replace(AND_REPLACEMENT, AND)
            .replace(APOS_REPLACEMENT, APOS)
            .replace(QUOTE_REPLACEMENT, QUOTE);
    }
}
