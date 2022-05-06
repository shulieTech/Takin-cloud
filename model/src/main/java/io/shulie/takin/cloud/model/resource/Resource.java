package io.shulie.takin.cloud.model.resource;

import lombok.Data;

/**
 * 资源
 * <ul>
 *     <li>CPU</li>
 *     <li>内存</li>
 * </ul>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public class Resource {
    /**
     * CPU
     * <p>1代表一个逻辑核</p>
     * <p>0.1代表100微核</p>
     */
    Double cpu;
    /**
     * 内存
     * <p>1代码1Kb</p>
     */
    Long memory;
}
