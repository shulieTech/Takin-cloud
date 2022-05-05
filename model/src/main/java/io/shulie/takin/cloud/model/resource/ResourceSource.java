package io.shulie.takin.cloud.model.resource;

import lombok.Data;

/**
 * 资源 - String
 * <ul>
 *     <li>CPU</li>
 *     <li>内存</li>
 * </ul>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public class ResourceSource {
    /**
     * CPU
     * <p>1代表一个逻辑核</p>
     */
    String cpu;
    /**
     * 内存
     * <p>1代码1Kb</p>
     */
    String memory;
}
