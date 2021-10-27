package io.shulie.takin.ext.content.script;

import io.shulie.takin.ext.content.AbstractEntry;
import io.shulie.takin.ext.content.emus.NodeTypeEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: liyuanba
 * @Date: 2021/10/26 11:29 上午
 */
@Data
public class ScriptNode extends AbstractEntry {
    /**
     * 节点名称
     */
    private String name;
    /**
     * 节点的testname属性内容
     */
    private String testName;
    /**
     * 元素节点的md5值
     */
    private String md5;
    /**
     * 类型
     */
    private NodeTypeEnum type;
    /**
     * 元素的绝对路劲
     */
    private String xpath;
    /**
     * xpath的md5
     */
    private String xpathMd5;
    /**
     * 属性信息
     */
    private Map<String, String> props;
    /**
     * 标识
     */
    private String identification;
    /**
     * 子节点
     */
    private List<ScriptNode> children;
}
