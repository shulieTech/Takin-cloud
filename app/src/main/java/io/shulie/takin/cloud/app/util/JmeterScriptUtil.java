package io.shulie.takin.cloud.app.util;

import cn.hutool.core.collection.CollectionUtil;
import io.shulie.takin.cloud.model.script.ScriptData;
import io.shulie.takin.cloud.model.script.ScriptHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.print.Doc;
import java.net.URL;
import java.util.List;
import java.util.Objects;


/**
 * ClassName:    JmeterScriptUtil
 * Package:    io.shulie.takin.cloud.app.util
 * Description:
 * Datetime:    2022/5/24   15:48
 * Author:   chenhongqiao@shulie.com
 */
@Slf4j
public class JmeterScriptUtil {

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


    public static Element buildBase(Document element) {
        Element plan = element.addElement("jmeterTestPlan");
        plan.addAttribute("version", "1.2");
        plan.addAttribute("properties", "5.0");
        plan.addAttribute("jmeter", "5.4.1");
        Element hashTree = plan.addElement("hashTree");
        return hashTree;
    }

    public static Element buildTestPlan(Element element, String testName) {
        Element plan = element.addElement("TestPlan");
        plan.addAttribute("guiclass", "TestPlanGui");
        plan.addAttribute("testclass", "TestPlan");
        plan.addAttribute("testname", testName);
        plan.addAttribute("enabled", "true");

        Element stringProp = plan.addElement("stringProp");
        stringProp.addAttribute("name", "TestPlan.comments");

        Element boolProp = plan.addElement("boolProp");
        boolProp.addAttribute("name", "TestPlan.functional_mode");
        boolProp.setText("false");

        Element boolProp2 = plan.addElement("boolProp");
        boolProp2.addAttribute("name", "TestPlan.tearDown_on_shutdown");
        boolProp2.setText("true");

        Element boolProp3 = plan.addElement("boolProp");
        boolProp3.addAttribute("name", "TestPlan.serialize_threadgroups");
        boolProp3.setText("false");

        Element elementProp = plan.addElement("elementProp");
        elementProp.addAttribute("name", "TestPlan.user_defined_variables");
        elementProp.addAttribute("elementType", "Arguments");
        elementProp.addAttribute("guiclass", "ArgumentsPanel");
        elementProp.addAttribute("testclass", "Arguments");
        elementProp.addAttribute("testname", "用户定义的变量");
        elementProp.addAttribute("enabled", "true");

        Element collectionProp = elementProp.addElement("collectionProp");
        collectionProp.addAttribute("name", "Arguments.arguments");

        Element stringProp2 = plan.addElement("stringProp");
        stringProp2.addAttribute("name", "TestPlan.user_define_classpath");


        Element hashTree = element.addElement("hashTree");
        return hashTree;
    }

    public static Element buildThreadGroup(Element element) {
        Element threadGroup = element.addElement("ThreadGroup");
        threadGroup.addAttribute("guiclass", "ThreadGroupGui");
        threadGroup.addAttribute("testclass", "ThreadGroup");
        threadGroup.addAttribute("testname", "线程组");
        threadGroup.addAttribute("enabled", "true");

        Element stringProp = threadGroup.addElement("stringProp");
        stringProp.addAttribute("name", "ThreadGroup.on_sample_error");
        stringProp.setText("continue");

        Element elementProp = threadGroup.addElement("elementProp");
        elementProp.addAttribute("name", "ThreadGroup.main_controller");
        elementProp.addAttribute("elementType", "LoopController");
        elementProp.addAttribute("guiclass", "LoopControlPanel");
        elementProp.addAttribute("testclass", "LoopController");
        elementProp.addAttribute("testname", "循环控制器");
        elementProp.addAttribute("enabled", "true");

        Element loopBoolProp = elementProp.addElement("boolProp");
        loopBoolProp.addAttribute("name", "LoopController.continue_forever");
        loopBoolProp.setText("false");

        Element loopStringProp = elementProp.addElement("stringProp");
        loopStringProp.addAttribute("name", "LoopController.loops");
        loopStringProp.setText("1");

        Element stringProp2 = threadGroup.addElement("stringProp");
        stringProp2.addAttribute("name", "ThreadGroup.num_threads");
        stringProp2.setText("1");

        Element stringProp3 = threadGroup.addElement("stringProp");
        stringProp3.addAttribute("name", "ThreadGroup.ramp_time");
        stringProp3.setText("1");

        Element boolProp = threadGroup.addElement("boolProp");
        boolProp.addAttribute("name", "ThreadGroup.scheduler");
        boolProp.setText("false");

        Element stringProp4 = threadGroup.addElement("stringProp");
        stringProp4.addAttribute("name", "ThreadGroup.duration");
        stringProp4.setText("");

        Element stringProp5 = threadGroup.addElement("stringProp");
        stringProp5.addAttribute("name", "ThreadGroup.delay");
        stringProp5.setText("");

        Element boolProp2 = threadGroup.addElement("boolProp");
        boolProp2.addAttribute("name", "ThreadGroup.same_user_on_next_iteration");
        boolProp2.setText("true");

        Element hashTree = element.addElement("hashTree");
        return hashTree;
    }

    public static Element buildHttpHeader(Element element, List<ScriptHeader> headers) {
        Element header = element.addElement("HeaderManager");
        header.addAttribute("guiclass", "HeaderPanel");
        header.addAttribute("testclass", "HeaderManager");
        header.addAttribute("testname", "HTTP信息头管理器");
        header.addAttribute("enabled", "true");

        Element collectionProp = header.addElement("collectionProp");
        collectionProp.addAttribute("name", "HeaderManager.headers");
        if (!CollectionUtil.isEmpty(headers)) {
            for (ScriptHeader scriptHeader : headers) {
                Element elementProp = collectionProp.addElement("elementProp");
                elementProp.addAttribute("name", "");
                elementProp.addAttribute("elementType", "Header");
                Element stringPropKey = elementProp.addElement("stringProp");
                stringPropKey.addAttribute("name", "Header.name");
                stringPropKey.setText(scriptHeader.getKey());

                Element stringPropVal = elementProp.addElement("stringProp");
                stringPropVal.addAttribute("name", "Header.value");
                stringPropVal.setText(scriptHeader.getValue());
            }
        }
        element.addElement("hashTree");
        return element;
    }

    public static Element buildCsvData(Element element, List<ScriptData> datas) {
        if (CollectionUtil.isEmpty(datas)) {
            return element;
        }
        for (ScriptData scriptData : datas) {
            Element data = element.addElement("CSVDataSet");
            data.addAttribute("guiclass", "TestBeanGUI");
            data.addAttribute("testclass", "CSVDataSet");
            data.addAttribute("testname", scriptData.getName());
            data.addAttribute("enabled", "true");

            Element stringProp = data.addElement("stringProp");
            stringProp.addAttribute("name", "filename");
            stringProp.setText(scriptData.getPath());

            Element fileEncoding = data.addElement("stringProp");
            fileEncoding.addAttribute("name", "fileEncoding");
            fileEncoding.setText("");

            Element variableNames = data.addElement("stringProp");
            variableNames.addAttribute("name", "variableNames");
            variableNames.setText(scriptData.getFormat());

            Element ignoreFirstLine = data.addElement("boolProp");
            ignoreFirstLine.addAttribute("name", "ignoreFirstLine");
            ignoreFirstLine.setText(Objects.isNull(scriptData.getIgnoreFirstLine()) ? "false" : scriptData.getIgnoreFirstLine().toString());

            Element delimiter = data.addElement("stringProp");
            delimiter.addAttribute("name", "delimiter");
            delimiter.setText(",");

            Element quotedData = data.addElement("boolProp");
            quotedData.addAttribute("name", "quotedData");
            quotedData.setText("false");

            Element recycle = data.addElement("boolProp");
            recycle.addAttribute("name", "recycle");
            recycle.setText("true");

            Element stopThread = data.addElement("boolProp");
            stopThread.addAttribute("name", "stopThread");
            stopThread.setText("false");

            Element shareMode = data.addElement("stringProp");
            shareMode.addAttribute("name", "shareMode");
            shareMode.setText("shareMode.all");

            element.addElement("hashTree");
        }

        return element;
    }

    public static Element buildHttpSampler(Element element, URL queryUrl, String method, String body) {

        Element sampler = element.addElement("HTTPSamplerProxy");
        sampler.addAttribute("guiclass", "HttpTestSampleGui");
        sampler.addAttribute("testclass", "HTTPSamplerProxy");
        sampler.addAttribute("testname", "HTTP请求");
        sampler.addAttribute("enabled", "true");
        if (StringUtils.isNotBlank(body)) {
            Element postBodyRaw = sampler.addElement("boolProp");
            postBodyRaw.addAttribute("name", "HTTPSampler.postBodyRaw");
            postBodyRaw.setText("true");

            Element arguments = sampler.addElement("elementProp");
            arguments.addAttribute("name", "HTTPsampler.Arguments");
            arguments.addAttribute("elementType", "Arguments");

            Element collectionProp = arguments.addElement("collectionProp");
            collectionProp.addAttribute("name", "Arguments.arguments");

            Element elementProp = collectionProp.addElement("elementProp");
            elementProp.addAttribute("name", "");
            elementProp.addAttribute("elementType", "HTTPArgument");

            Element boolProp = elementProp.addElement("boolProp");
            boolProp.addAttribute("name", "HTTPArgument.always_encode");
            boolProp.setText("false");

            Element stringProp = elementProp.addElement("stringProp");
            stringProp.addAttribute("name", "Argument.value");
            stringProp.setText(body);

            Element stringProp2 = elementProp.addElement("stringProp");
            stringProp2.addAttribute("name", "Argument.metadata");
            stringProp2.setText("=");
        }

        Element domain = sampler.addElement("stringProp");
        domain.addAttribute("name", "HTTPSampler.domain");
        domain.setText(queryUrl.getHost());

        Element port = sampler.addElement("stringProp");
        port.addAttribute("name", "HTTPSampler.port");
        port.setText(queryUrl.getPort() + "");

        Element protocol = sampler.addElement("stringProp");
        protocol.addAttribute("name", "HTTPSampler.protocol");
        protocol.setText(queryUrl.getProtocol());

        Element contentEncoding = sampler.addElement("stringProp");
        contentEncoding.addAttribute("name", "HTTPSampler.contentEncoding");
        contentEncoding.setText("UTF-8");

        Element path = sampler.addElement("stringProp");
        path.addAttribute("name", "HTTPSampler.path");
        StringBuilder pathQuery = new StringBuilder(queryUrl.getPath());
        if(StringUtils.isNotBlank(queryUrl.getQuery())) {
            pathQuery.append("?").append(queryUrl.getQuery());
        }
        path.setText(pathQuery.toString());

        Element methodE = sampler.addElement("stringProp");
        methodE.addAttribute("name", "HTTPSampler.method");
        methodE.setText(method);

        Element follow_redirects = sampler.addElement("boolProp");
        follow_redirects.addAttribute("name", "HTTPSampler.follow_redirects");
        follow_redirects.setText("true");

        Element auto_redirects = sampler.addElement("boolProp");
        auto_redirects.addAttribute("name", "HTTPSampler.auto_redirects");
        auto_redirects.setText("false");

        Element use_keepalive = sampler.addElement("boolProp");
        use_keepalive.addAttribute("name", "HTTPSampler.use_keepalive");
        use_keepalive.setText("true");

        Element do_multipart_post = sampler.addElement("boolProp");
        do_multipart_post.addAttribute("name", "HTTPSampler.DO_MULTIPART_POST");
        do_multipart_post.setText("false");

        Element embedded_url_re = sampler.addElement("stringProp");
        embedded_url_re.addAttribute("name", "HTTPSampler.embedded_url_re");
        embedded_url_re.setText("");

        Element connect_timeout = sampler.addElement("stringProp");
        connect_timeout.addAttribute("name", "HTTPSampler.connect_timeout");
        connect_timeout.setText("");

        Element response_timeout = sampler.addElement("stringProp");
        response_timeout.addAttribute("name", "HTTPSampler.response_timeout");
        response_timeout.setText("");

        element.addElement("hashTree");
        return element;
    }

    public static String getDocumentStr(Document document) {
        StringWriter stringWriter = null;
        try {
            stringWriter = new StringWriter();
            document.write(stringWriter);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            if (stringWriter != null) {
                try {
                    stringWriter.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }

        String finalStr = stringWriter.getString();
        finalStr = specialCharRepAfter(finalStr);

        return finalStr;
    }

    public static String specialCharRepAfter(String content) {
        content = content.replaceAll(LESS_THAN_REPLACEMENT, LESS_THAN);
        content = content.replaceAll(GREATER_THAN_REPLACEMENT, GREATER_THAN);
        content = content.replaceAll(AND_REPLACEMENT, AND);
        content = content.replaceAll(APOS_REPLACEMENT, APOS);
        content = content.replaceAll(QUOTE_REPLACEMENT, QUOTE);
        return content;
    }

}
