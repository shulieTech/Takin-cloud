package io.shulie.plugin.engine.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.io.FileUtil;
import io.shulie.plugin.engine.jmeter.XmlDubboJmxParser;
import io.shulie.plugin.engine.jmeter.XmlHttpJmxParser;
import io.shulie.plugin.engine.jmeter.XmlJdbcJmxParser;
import io.shulie.plugin.engine.jmeter.XmlKafkaJmxParser;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.FileUtils;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptUrlExt;
import io.shulie.takin.utils.file.FileManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * <ol>
 *     <li>获取jmx文件内容</li>
 *     <li>获取http接口数量</li>
 *     <li>循环获取http的name和url</li>
 *     <li>获取该http对应的header信息
 *     <p>
 * 通过字符串截取先获得整个<headerManager></headerManager>
 * 非启用状态的header不纳入计算范围
 * 再获取header里面的method(顺丰的请求url会放到这里），和User-Agent信息
 * </p>
 * </li>
 *     <li>替换url</li>
 * </ol>
 *
 * @author qianshui
 * @date 2020/4/22 上午4:09
 */
@Slf4j
public class SaxUtil {

    public static ScriptParseExt parseJmx(String path) {
        SAXReader saxReader = new SAXReader();
        ScriptParseExt scriptParseExt = new ScriptParseExt();
        try {
            //文件路径安全处理
            FileUtils.verityFilePath(path);
            File file = FileUtil.file("/data" + path);
            String content = FileManagerHelper.readFileToString(file, "utf-8");
            // 读取文件内容
            Document document = saxReader.read(file);
            // 去除所有禁用节点和对应的所有子节点
            cleanAllDisableElement(document);
            List<ScriptUrlExt> scriptUrls = getScriptUrlFromJmx(scriptParseExt, content, document);
            scriptParseExt.setRequestUrl(scriptUrls);
        } catch (DocumentException e) {
            log.error("异常代码【{}】,异常内容：DocumentException", TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：IOException", TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
            throw new TakinCloudException(TakinCloudExceptionEnum.XML_PARSE_ERROR, "IOException", e);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：解析JMeter脚本错误。", TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
            throw new TakinCloudException(TakinCloudExceptionEnum.XML_PARSE_ERROR, "Exception", e);
        }
        return scriptParseExt;
    }

    private static List<ScriptUrlExt> getXmlJdbcContent(Document document, String content, ScriptParseExt scriptParseExt) {
        XmlJdbcJmxParser parser = new XmlJdbcJmxParser();
        return parser.getEntryContent(document, content, scriptParseExt);
    }

    private static List<ScriptUrlExt> getXmlKafkaContent(Document document, String content, ScriptParseExt scriptParseExt) {
        XmlKafkaJmxParser parser = new XmlKafkaJmxParser();
        return parser.getEntryContent(document, content, scriptParseExt);
    }

    public static void updateJmx(String path) {
        SAXReader saxReader = new SAXReader();
        try {
            //文件路径安全处理
            path = FilenameUtils.getFullPath(path) + FilenameUtils.getName(path);
            File file = new File(path);
            String content = FileManagerHelper.readFileToString(file, "utf-8");
            Document document = saxReader.read(new File(path));
            //去除所有禁用节点和对应的所有子节点
            cleanAllDisableElement(document);
            getScriptUrlFromJmx(new ScriptParseExt(), content, document);
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, new OutputFormat());
            xmlWriter.write(document);
            xmlWriter.close();
            String xmlContent = writer.toString();
            FileManagerHelper.deleteFilesByPath(path);
            FileManagerHelper.createFileByPathAndString(path, xmlContent);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：Parse Jmeter Script Error --> 异常信息: {}",
                TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
        }
    }

    private static List<ScriptUrlExt> getScriptUrlFromJmx(ScriptParseExt scriptParseExt, String content, Document document) {
        List<ScriptUrlExt> scriptUrls = new ArrayList<>();
        List<ScriptUrlExt> xmlHttpContent = getXmlHttpContent(document, content, scriptParseExt);
        List<ScriptUrlExt> dubboContents = getXmlDubboContent(document, content, scriptParseExt);
        List<ScriptUrlExt> kafkaContents = getXmlKafkaContent(document, content, scriptParseExt);
        List<ScriptUrlExt> jdbcContents = getXmlJdbcContent(document, content, scriptParseExt);

        if (CollectionUtils.isNotEmpty(xmlHttpContent)) {
            scriptUrls.addAll(xmlHttpContent);
        }
        if (CollectionUtils.isNotEmpty(dubboContents)) {
            scriptUrls.addAll(dubboContents);
        }
        if (CollectionUtils.isNotEmpty(kafkaContents)) {
            scriptUrls.addAll(kafkaContents);
        }
        if (CollectionUtils.isNotEmpty(jdbcContents)) {
            scriptUrls.addAll(jdbcContents);
        }
        log.info("jmx parser start ==================");
        scriptUrls.forEach((scriptUrlVO) -> log.info(JSON.toJSONString(scriptUrlVO)));
        log.info("jmx parser end ==================");
        return scriptUrls;
    }

    private static List<ScriptUrlExt> getXmlDubboContent(Document document, String content, ScriptParseExt scriptParseExt) {
        XmlDubboJmxParser parser = new XmlDubboJmxParser();
        return parser.getEntryContent(document, content, scriptParseExt);
    }

    private static List<ScriptUrlExt> getXmlHttpContent(Document document, String content, ScriptParseExt scriptParseExt) {
        XmlHttpJmxParser httpJmxParser = new XmlHttpJmxParser();
        return httpJmxParser.getEntryContent(document, content, scriptParseExt);
    }

    public static List<Element> getAllElement(String elementName, Document document) {
        List<Element> result = new ArrayList<>();
        Element rootElement = document.getRootElement();
        selectElement(elementName, rootElement.elements(), result);
        return result;
    }

    public static void cleanAllDisableElement(Document document) {
        Element rootElement = document.getRootElement();
        cleanDisableElement(rootElement.elements());
    }

    public static void cleanDisableElement(List elements) {
        if (CollectionUtils.isNotEmpty(elements)) {
            for (int i = 0; i < elements.size(); i++) {
                Element element = (Element)elements.get(i);
                cleanDisableElement(element.elements());
                if (element.attributeValue("enabled") != null && !"true".equals(element.attributeValue("enabled"))) {
                    if (elements.size() > i + 1) {
                        Element nextElement = (Element)elements.get(i + 1);
                        if ("hashTree".equals(nextElement.getName())) {
                            elements.remove(nextElement);
                        }
                    }
                    elements.remove(element);
                    i--;
                }
            }
        }
    }

    public static Element selectElementByEleNameAndAttr(String elementName, String attributeName, String attributeValue,
        List elements) {
        if (CollectionUtils.isEmpty(elements)) {
            return null;
        }
        for (Object it : elements) {
            Element element = (Element)it;
            if (element.getName().equals(elementName) && attributeValue.equals(element.attributeValue(attributeName))) {
                return element;
            }
            Element childElement = selectElementByEleNameAndAttr(elementName, attributeName, attributeValue,
                element.elements());
            if (childElement != null) {
                return childElement;
            }
        }
        return null;
    }

    public static void selectElement(String elementName, List elements, List<Element> result) {
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        for (Object o : elements) {
            Element element = (Element)o;
            if (element.getName().equals(elementName)) {
                result.add(element);
            }
            List childElements = element.elements();
            selectElement(elementName, childElements, result);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String path = "/Users/shulie/Documents/test.jmx";
        //        SaxUtil.updatePressTestTags(path);
        File file = new File(path);
        //因为新增场景脚本是异步的，这里最多等待5分钟
        int i = 0;
        while (!file.exists()) {
            i++;
            Thread.sleep(100L);
            if (i > 3000) {
                return;
            }
        }
        System.out.println("有文件");
    }

    /**
     * 将dubbo压测标的值从true修改为false
     * 将http的压测标从PerfomanceTest 修改为flowDebug
     *
     * @param path 路径
     */
    public static void updatePressTestTags(String path) {
        SAXReader saxReader = new SAXReader();
        try {
            //文件路径安全处理
            path = FilenameUtils.getFullPath(path) + FilenameUtils.getName(path);
            File file = new File(path);
            //因为新增场景脚本是异步的，这里最多等待5分钟
            int i = 0;
            while (!file.exists()) {
                i++;
                Thread.sleep(100L);
                if (i > 3000) {
                    return;
                }
            }

            // 读取文件内容
            Document document = saxReader.read(file);
            //去除所有禁用节点和对应的所有子节点
            cleanAllDisableElement(document);
            updateJmxHttpPressTestTags(document);
            updateXmlDubboPressTestTags(document);

            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, new OutputFormat());
            xmlWriter.write(document);
            xmlWriter.close();
            String xmlContent = writer.toString();
            FileManagerHelper.deleteFilesByPath(path);
            FileManagerHelper.createFileByPathAndString(path, xmlContent);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：Parse Jmeter Script Error --> 异常信息: {}",
                TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
        }
    }

    private static void updateXmlDubboPressTestTags(Document document) {

        List<Element> allElement = getAllElement("io.github.ningyu.jmeter.plugin.dubbo.sample.DubboSample", document);
        for (Element element : allElement) {
            List<Element> stringPropList = new ArrayList<>();
            selectElement("stringProp", element.elements(), stringPropList);
            if (CollectionUtils.isNotEmpty(stringPropList)) {
                String attachmentArgsValue = "";
                for (Element ele : stringPropList) {
                    if (ele.attributeValue("name") != null && ele.attributeValue("name").startsWith(
                        "FIELD_DUBBO_ATTACHMENT_ARGS_KEY")
                        && "p-pradar-cluster-test".equals(ele.getText())) {
                        String attributeValue = ele.attributeValue("name");
                        attachmentArgsValue = attributeValue.replace("KEY", "VALUE");
                    }
                }
                if (StringUtils.isNotBlank(attachmentArgsValue)) {
                    Element dubboAttachmentValue = selectElementByEleNameAndAttr("stringProp", "name",
                        attachmentArgsValue, element.elements());
                    if (dubboAttachmentValue != null && "true".equals(dubboAttachmentValue.getText())) {
                        dubboAttachmentValue.setText("false");
                    }
                }
            }
        }
    }

    public static void updateJmxHttpPressTestTags(Document document) {
        List<Element> allElement = getAllElement("HeaderManager", document);
        if (CollectionUtils.isNotEmpty(allElement)) {
            List<Element> allElementProp = new ArrayList<>();
            for (Element headerElement : allElement) {
                selectElement("elementProp", headerElement.elements(), allElementProp);
            }
            if (CollectionUtils.isNotEmpty(allElementProp)) {
                for (Element elementProp : allElementProp) {
                    Element nameElement = selectElementByEleNameAndAttr("stringProp", "name", "Header.name",
                        elementProp.elements());
                    Element valueElement = selectElementByEleNameAndAttr("stringProp", "name", "Header.value",
                        elementProp.elements());
                    if (nameElement != null && valueElement != null && "User-Agent".equals(nameElement.getText())
                        && "PerfomanceTest".equals(valueElement.getText())) {
                        valueElement.setText("FlowDebug");
                    }
                }
            }
        }
    }

}
