package io.shulie.plugin.engine.jmeter;

import java.util.List;

import io.shulie.takin.ext.content.script.ScriptParseExt;
import io.shulie.takin.ext.content.script.ScriptUrlExt;
import org.dom4j.Document;

/**
 * @author HengYu
 * @className JmxParse
 * @date 2021/4/12 4:02 下午
 * @description
 */
public abstract class JmxParser {

    /**
     * 获取Jmeter 脚本请求入口
     * @param document JMX对应文档对象
     * @param content 脚本原文
     * @param ptSize pt 数量对象
     * @return
     */
    abstract List<ScriptUrlExt> getEntryContent(Document document, String content, ScriptParseExt scriptParseExt);
}
